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
	 * Will list all connected devices.
	 *
	 * @return A list of devices or an empty list.
	 */
	@SuppressWarnings("GroovyMissingReturnStatement")
	List<ClewareControlDevice> listDevices(def filterType=null) {
		List<ClewareControlDevice> devices = [];
		Process process = clewarecontrol "-l"

		process.inputStream.eachLine { line ->
			log.info(line);
			// We are only interested in lines which contain actual device information.
			if (line.startsWith('Device')) {
				// Device: 0, type: Switch1 (8), version: 106, serial number: 902492
				ClewareControlDevice device = createDeviceFromCommandLine(line)

				devices.push(device)
			}
		}
		process.waitFor()

		// filter the list, if needed.
		if( null != filterType ) {
			return devices.grep(filterType)
		}

		devices
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
	 * Creates  and initializes a TrafficLightsDevice.
	 */
	@SuppressWarnings("GroovyMissingReturnStatement")
	TrafficLightsDevice createTrafficLightsDevice(int id) {
		log.info("Creating traffic lights device.")
		// first create a basic device
		def device = new TrafficLightsDevice(id: id)

		// request the state from the command line.
		Process process = clewarecontrol "-c", "1", "-d", "${id}", "-rs", "0", "-rs", "1", "-rs", "2"
		def counter = 0;
		process.inputStream.splitEachLine(/\s/) { splitLine ->
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
		def exitValue = process.waitFor()

		// check the exit value
		if( 0 == exitValue ) {
			return device
		} else {
			throw new DeviceNotFoundException("It looks like there is no device with id ${id}.")
		}
	}

	/**
	 * This will update all three switches of the device
	 *
	 * @param device
	 */
	def updateTrafficLights(TrafficLightsDevice device) {
		Process process = clewarecontrol "-c", "1", "-d", "${device.id}",
		                                 "-as", "0", "${device.r?1:0}",
		                                 "-as", "1", "${device.y?1:0}",
		                                 "-as", "2", "${device.g?1:0}"
		process.inputStream.eachLine { line ->
				log.debug "${line}"
		}
		def exitValue = process.waitFor()

		// check the exit value
		if((0 != exitValue)) {
			throw new DeviceNotFoundException("It looks like there is no device with id ${device.id}.")
		}
	}

	private clewarecontrol(String... cmd) {
		def process
		try {
			// convert the array into a list for easy processing
			def list = cmd as List
			list.add 0, "clewarecontrol"
			process = starter.start(list as String[])
		} catch (e) {
			log.error("Something went wrong when calling the binary.", e);
			throw new BinaryNotFoundException("Clewarecontrol binary not found. Are you sure you installed it?",
			                                  e.getCause())
		}
		process
	}
}
