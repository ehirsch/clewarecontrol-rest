package net.eikehirsch.clewarecontrol

import net.eikehirsch.clewarecontrol.device.ClewareControlDevice
import net.eikehirsch.clewarecontrol.exception.BinaryNotFoundException
import net.eikehirsch.clewarecontrol.process.ProcessStarter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * This class is used to wrap all calls to the clewarecontrol binary.
 */
class ClewareControl {

  static final Logger LOG = LoggerFactory.getLogger(ClewareControl.class );

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
  List<ClewareControlDevice> list() {
	List<ClewareControlDevice> devices = [];
	String[] cmd = [CLEWARECONTROL, "-l"]
    def process
    try {
		process = starter.start(cmd)
	} catch (e) {
	    throw new BinaryNotFoundException("Clewarecontrol binary not found. Are you sure you installed it?", e.getCause())
	}
	process.inputStream.eachLine { line ->
	  LOG.info(line);
	  // We are only interested in lines which contain actual device information.
	  if( line.startsWith('Device') ) {
		// Device: 0, type: Switch1 (8), version: 106, serial number: 902492

	  }
	}
    process.waitFor()

	return devices;
  }


}
