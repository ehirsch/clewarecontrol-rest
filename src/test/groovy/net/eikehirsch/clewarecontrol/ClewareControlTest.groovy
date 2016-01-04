package net.eikehirsch.clewarecontrol
import net.eikehirsch.clewarecontrol.exception.BinaryNotFoundException
import net.eikehirsch.clewarecontrol.process.ProcessStarter
import net.eikehirsch.clewarecontrol.trafficlights.TrafficLightsDevice
import net.eikehirsch.clewarecontrol.usage.UnknownDevice
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail
/**
 * @author Eike Hirsch
 *         Date: 22.11.15
 *         Time: 14:32
 */
class ClewareControlTest {

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
	
	void mockClewareControlList() {
		mockClewareControl(["clewarecontrol", "-l"], """
			Cleware library version: 330
			Number of Cleware devices found: 3
			Device: 0, type: Switch1 (8), version: 106, serial number: 902492
			Device: 1, type: Switch1 (8), version: 106, serial number: 902493
			Device: 2, type: Unknown, version: 106, serial number: 902494
			""")
	}

	private mockClewareControl(commandLine, commandOutput) {
		Process processMock = mockProcess(0, commandOutput)
		ProcessStarter processStarterMock = mockProcessStarter(commandLine, processMock)
		ctrl = new ClewareControl(processStarterMock)
	}

	@Test
	void failsWithExceptionIfNoRuntimeWasFound(){

		def starterMock = [
				start: { cmd ->
					throw new IOException("Cannot run program \"clewarecontrol\": error=2, No such file or directory")
				}
		] as ProcessStarter

		ctrl = new ClewareControl(starterMock)
		def ex = shouldFail(BinaryNotFoundException.class) { ctrl.list() }

		assert ex.message == "Clewarecontrol binary not found. Are you sure you installed it?"
	}


	// ### list all devices

	@Test
	void emptyList(){

		Process processMock = mockProcess(0,"""
			Cleware library version: 330
			Number of Cleware devices found: 0
			""")

		ProcessStarter processStarterMock = mockProcessStarter(["clewarecontrol", "-l"], processMock)

		ctrl = new ClewareControl(processStarterMock)
		def list = ctrl.list()

		assert list.isEmpty()
	}



	@Test
	void listAllDevicesAreReturned(){
		mockClewareControlList()
		def list = ctrl.list()

		assert list.size() == 3
	}

	@Test
	void listAllDevicesAreInitialized(){
		mockClewareControlList()
		def list = ctrl.list()

		assert list[0].id == 902492
		assert list[1].id == 902493
		assert list[2].id == 902494
	}

	@Test
	void listShouldCreateDevicesOfCorrectType(){
		mockClewareControlList()
		def list = ctrl.list()

		assert list[0] instanceof TrafficLightsDevice
		assert list[1] instanceof TrafficLightsDevice
		assert list[2] instanceof UnknownDevice
	}


	@Test
	void listShouldBeAbleToFilterDevicesByType(){
		mockClewareControlList()
		def list = ctrl.list(TrafficLightsDevice.class)

		assert list.size() == 2
		assert list[0] instanceof TrafficLightsDevice
		assert list[1] instanceof TrafficLightsDevice
	}

	// ### traffic lights

	@Test
	void shouldCreateAndInitializeAnIdentifiedTrafficLightsDevice() {
		mockClewareControl(["clewarecontrol", "-c", "1", "-d", "42", "-rs", "0", "-rs", "1", "-rs", "2"], """
			Status: Off (0)
			Status: On (1)
			Status: Off (0)
		""")
		def device = ctrl.createTrafficLights(42);

		assert device.id == 42
		assert !device.r
		assert device.y
		assert !device.g

		// and again
		mockClewareControl(["clewarecontrol", "-c", "1", "-d", "42", "-rs", "0", "-rs", "1", "-rs", "2"], """
			Status: On (1)
			Status: Off (0)
			Status: On (1)
		""")
		device = ctrl.createTrafficLights(42);

		assert device.id == 42
		assert device.r
		assert !device.y
		assert device.g

	}


}