package net.eikehirsch.clewarecontrol
import net.eikehirsch.clewarecontrol.exception.BinaryNotFoundException
import net.eikehirsch.clewarecontrol.process.ProcessStarter
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail
/**
 * @author Eike Hirsch
 *         Date: 22.11.15
 *         Time: 14:32
 */
public class ClewareControlTest {


	private mockProcess(exitValue, output) {
		def processMock = [
				waitFor       : {exitValue},
				getInputStream: { new ByteArrayInputStream(output.stripIndent().bytes) }
		] as Process
		processMock
	}

	private mockProcessStarter(commands, processMock) {
		def starterMock = [
				start: { cmd ->
					assert cmd == commands
					return processMock
				}
		] as ProcessStarter
		starterMock
	}

	@Test
	public void failsWithExceptionIfNoRuntimeWasFound() throws Exception {

		def starterMock = [
				start: { cmd ->
					throw new IOException("Cannot run program \"clewarecontrol\": error=2, No such file or directory")
				}
		] as ProcessStarter

		def ctrl = new ClewareControl(starterMock);
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

		def ctrl = new ClewareControl(processStarterMock);
		def list = ctrl.list();

		assert list.isEmpty();
	}


}