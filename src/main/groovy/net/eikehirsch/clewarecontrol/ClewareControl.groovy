package net.eikehirsch.clewarecontrol

import net.eikehirsch.clewarecontrol.exception.BinaryNotFoundException
import net.eikehirsch.clewarecontrol.process.ProcessStarter
import net.eikehirsch.clewarecontrol.trafficlights.TrafficLightsDevice
import net.eikehirsch.clewarecontrol.usage.UnknownDevice
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This class is used to wrap all calls to the clewarecontrol binary.
 */
class ClewareControl {

	static final Logger LOG = LoggerFactory.getLogger(ClewareControl.class);

	static final String CLEWARECONTROL = "clewarecontrol"

	private ProcessStarter starter


	ClewareControl(ProcessStarter starter) {
		this.starter = starter
	}

	/**
	 * Will list all connected devices.
	 *
	 * @return A list of devices or an empty list.
	 */
	List<ClewareControlDevice> list(def filterType=null) {
		List<ClewareControlDevice> devices = [];
		String[] cmd = [CLEWARECONTROL, "-l"]
		def process
		try {
			process = starter.start(cmd)
		} catch (e) {
			throw new BinaryNotFoundException("Clewarecontrol binary not found. Are you sure you installed it?", e.getCause())
		}
		process.inputStream.eachLine { String line ->
			LOG.info(line);
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

		return devices;
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
			LOG.debug("key: {}, value: {}", key, value)
			definition.put(key, value);
		}
		LOG.debug(definition.toString())
		// now we can create and return the actual device

		def device
		switch (definition.type) {
			case 'Switch1 (8)':
				device = new TrafficLightsDevice(id: Integer.valueOf(definition.serial_number),
				                                 version: Integer.valueOf(definition.version))
				break
			default:
				device = new UnknownDevice(id: Integer.valueOf(definition.serial_number),
				                           version: Integer.valueOf(definition.version))
		}

		device
	}


}
