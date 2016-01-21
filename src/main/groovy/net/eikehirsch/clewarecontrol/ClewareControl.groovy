package net.eikehirsch.clewarecontrol

import groovy.util.logging.Slf4j
import net.eikehirsch.clewarecontrol.exception.BinaryNotFoundException
import net.eikehirsch.clewarecontrol.exception.DeviceNotFoundException
import net.eikehirsch.clewarecontrol.process.ProcessStarter
import net.eikehirsch.clewarecontrol.trafficlights.TrafficLightsDevice
import net.eikehirsch.clewarecontrol.usage.UnknownDevice

/**
 * This class is used to wrap all calls to the clewarecontrol binary.
 */
@Slf4j
class ClewareControl {

	private ProcessStarter starter

	ClewareControl(ProcessStarter starter) {
		this.starter = starter
	}

	/**
	 * Helper class to process the output of the list command
	 */
	class DeviceCollector {
		def devices = [];

		Closure outputProcessor() {
			return { eachLine { String line ->
				log.info(line);
				// We are only interested in lines which contain actual device information.
				if (line.startsWith('Device')) {
					// Device: 0, type: Switch1 (8), version: 106, serial number: 902492
					ClewareControlDevice device = createDeviceFromCommandLine(line)

					devices.push(device)
					}
				}
			}
		}
	}


	/**
	 * Will list all connected devices.
	 *
	 * @return A list of devices or an empty list.
	 */
	List<ClewareControlDevice> listDevices(def filterType=null) {

		DeviceCollector collector = new DeviceCollector()

		clewarecontrol("-l", collector.outputProcessor() )

		// filter the list, if needed.
		if (null != filterType) {
			return collector.devices.grep(filterType)
		}

		collector.devices
	}

	/**
	 * This will create a device representation from the output of the command line program.
	 *
	 * @param line The current line. Has to be one containing actual device information.
	 *
	 * @return A device
	 */
	private static createDeviceFromCommandLine(String line) {
		def definition = [:]
		line.split(",").each { String it ->
			def (key, value) = it.split(':')
			// remove whitespace
			key = key.trim().replace(' ', '_')
			value = value.trim()
			log.debug("key: {}, value: {}", key, value)
			definition.put(key, value);
		}
		log.debug(definition.toString())
		// now we can create and return the actual device

		def device
		switch (definition.type) {
			case 'Switch1 (8)':
				// TODO: check if we are able to call another command here. (createTrafficLights)
				device = new TrafficLightsDevice(id: definition.serial_number.toInteger(),
				                                 version: definition.version.toInteger())
				break
			default:
				device = new UnknownDevice(id: definition.serial_number.toInteger(),
				                           version: definition.version.toInteger())
		}

		device
	}

	/**
	 * Helper class to create a TrafficLightsDevice.
	 */
	class TrafficLightsDeviceCreator {

		def device
		def counter = 0

		TrafficLightsDeviceCreator(device) {
			this.device = device
		}

		Closure outputProcessor() {
			return {
				splitEachLine(/\s/) { List splitLine ->
					if( 3 == splitLine.size() ) {
						log.debug("${splitLine}" )
						if( 0 == counter ) {
							// red
							device.r = 'On' == splitLine[1]
						} else if( 1 == counter ) {
							// yellow
							device.y = 'On' == splitLine[1]
						} else if( 2 == counter ) {
							// green
							device.g = 'On' == splitLine[1]
						}
						counter++
					}
				}
			}
		}

	}
	/**
	 * Creates  and initializes a TrafficLightsDevice.
	 */
	TrafficLightsDevice createTrafficLightsDevice(int id) {
		log.info("Creating traffic lights device.")

		def creator = new TrafficLightsDeviceCreator(new TrafficLightsDevice(id: id))

		clewarecontrol(id, "-rs 0 -rs 1 -rs 2", creator.outputProcessor())

		creator.device
	}


	/**
	 * This will update all three switches of the device
	 *
	 * @param device
	 */
	def updateTrafficLights(TrafficLightsDevice device) {
		clewarecontrol(device.id, "-as 0 ${device.r?1:0} -as 1 ${device.y?1:0} -as 2 ${device.g?1:0}") {
			eachLine { line ->
				log.debug "${line}"
			}
		}
	}

	/**
	 * This is actually going to start the native process and process its output.
	 *
	 * @param deviceId (optional) If provided will be used to identify the device
	 * @param cmd The actual command to be called
	 * @param outputParser A closure which delegate will become the input stream of the started process. The closure is
	 *                     mandatory!
	 */
	private clewarecontrol(deviceId = -1, String cmd, Closure outputParser) {
		Process process

		if( -1 != deviceId ) {
			cmd = "-c 1 -d ${deviceId} ${cmd}"
		}

		try {
			process = starter.start "clewarecontrol ${cmd}".split()
		} catch (e) {
			log.error("Something went wrong when calling the binary.", e);
			throw new BinaryNotFoundException("Clewarecontrol binary not found. Are you sure you installed it?",
			                                  e.getCause())
		}
		// do what ever is wanted
		process.inputStream.with outputParser

		// check the exit value
		if((0 != process.waitFor())) {
			if(-1 != deviceId)
				throw new DeviceNotFoundException("It looks like there is no device with id ${deviceId}.")
			// TODO: add an else exception
		}
	}

}
