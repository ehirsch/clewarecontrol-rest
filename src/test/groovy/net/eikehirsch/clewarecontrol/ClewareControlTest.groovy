package net.eikehirsch.clewarecontrol
import net.eikehirsch.clewarecontrol.exception.BinaryNotFoundException
import net.eikehirsch.clewarecontrol.process.ProcessStarter
import net.eikehirsch.clewarecontrol.device.TrafficLightsDevice
import net.eikehirsch.clewarecontrol.device.UnknownDevice
import org.junit.Before
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail
/**
 * @author Eike Hirsch
 *         Date: 22.11.15
 *         Time: 14:32
 */
public class ClewareControlTest {

	private ClewareControl ctrl


	static mockProcess(exitValue, output) {
		def processMock = [
				waitFor       : {exitValue},
				getInputStream: { new ByteArrayInputStream(output.stripIndent().bytes) }
		] as Process
		processMock
	}

	static mockProcessStarter(commands, processMock) {
		def starterMock = [
				start: { cmd ->
					assert cmd == commands
					return processMock
				}
		] as ProcessStarter
		starterMock
	}
	
	@Before
	void initClewareControlTestDefaultSetup() {
		Process processMock = mockProcess(0,"""
			Cleware library version: 330
			Number of Cleware devices found: 3
			Device: 0, type: Switch1 (8), version: 106, serial number: 902492
			Device: 1, type: Switch1 (8), version: 106, serial number: 902493
			Device: 2, type: Unknown, version: 106, serial number: 902494
			""")

		ProcessStarter processStarterMock = mockProcessStarter(["clewarecontrol", "-l"], processMock)

		ctrl = new ClewareControl(processStarterMock)
	}

	@Test
	public void failsWithExceptionIfNoRuntimeWasFound() throws Exception {

		def starterMock = [
				start: { cmd ->
					throw new IOException("Cannot run program \"clewarecontrol\": error=2, No such file or directory")
				}
		] as ProcessStarter

		ctrl = new ClewareControl(starterMock);
		def ex = shouldFail(BinaryNotFoundException.class) { ctrl.list(); }

		assert ex.message == "Clewarecontrol binary not found. Are you sure you installed it?"
	}

	@Test
	public void emptyList() throws Exception {

		Process processMock = mockProcess(0,"""
			Cleware library version: 330
			Number of Cleware devices found: 0
			""")

		ProcessStarter processStarterMock = mockProcessStarter(["clewarecontrol", "-l"], processMock)

		ctrl = new ClewareControl(processStarterMock);
		def list = ctrl.list();

		assert list.isEmpty();
	}

	@Test
	public void listAllDevicesAreReturned() throws Exception {

		def list = ctrl.list();

		assert list.size() == 3
	}

	@Test
	public void listAllDevicesAreInitialized() throws Exception {

		def list = ctrl.list();

		assert list[0].id == 902492
		assert list[1].id == 902493
		assert list[2].id == 902494
	}

	@Test
	public void listShouldCreateDevicesOfCorrectType() throws Exception {

		def list = ctrl.list();

		assert list[0] instanceof TrafficLightsDevice
		assert list[1] instanceof TrafficLightsDevice
		assert list[2] instanceof UnknownDevice
	}


	@Test
	public void listShouldBeAbleToFilterDevicesByType() throws Exception {

		def list = ctrl.list(TrafficLightsDevice.class);

		assert list.size() == 2
		assert list[0] instanceof TrafficLightsDevice
		assert list[1] instanceof TrafficLightsDevice
	}


}