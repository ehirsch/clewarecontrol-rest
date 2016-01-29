package net.eikehirsch.clewarecontrol

import net.eikehirsch.clewarecontrol.process.ProcessStarter

/**
 * @author Eike Hirsch
 * Date: 25.01.16
 * Time: 08:09
 */
class StubUtils {

	static ProcessStarter processStarterStub = [
		start: { String[] cmd ->
			Process process
			def commandLine = cmd.join(' ');
			switch (commandLine) {
			// result of the list command.
			// This list will grow as I get more knowledge about other devices available.
				case ~/.*-l/:
					process = ClewareControlTest.mockProcess(0, """
										Cleware library version: 330
										Number of Cleware devices found: 3
										Device: 0, type: Switch1 (8), version: 106, serial number: 902492
										Device: 1, type: Switch1 (8), version: 106, serial number: 902493
										Device: 2, type: Unknown, version: 106, serial number: 902494
									""")
					break;
			// result of the status call to the first traffic light. // This one is working normally
				case ~/.*-c 1 -d 90249\d -rs 0 -rs 1 -rs 2/:
					process = ClewareControlTest.mockProcess(0, """
										Status: Off (0)
										Status: Off (0)
										Status: On (1)
									""")
					break;
				case ~/.*-c 1 -d 123456 .*/:
					process = ClewareControlTest.mockProcess(1, """
										Device 123456 not found
										errno at that time: Bad file descriptor (9)
									""")
					break;
				case ~/.*-c 1 -d 902492 -as .*/:
					process = ClewareControlTest.mockProcess(0, """
										TODO: check for the real output
									""")
					break;

				default:
					throw new IllegalStateException(
							"This test was run with an unknown clewarecontrol command.");
			}
			process
		}
	] as ProcessStarter
}
