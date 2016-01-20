package net.eikehirsch.clewarecontrol
import net.eikehirsch.clewarecontrol.process.ProcessStarter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = ClewareControlApp)
@WebAppConfiguration
class ClewareControlAppTests {

	@Configuration
	static class ClewareControlMockingTestConfig {

		// Stub the runtime. We can't be sure that the binary will be in place when we run our tests.
		// What's more important is, that subbing the binary is the only way to guarantee a text fixture.
		@Bean
		@Primary
		ProcessStarter stubedProcessStarter() {
			def processStarter = [
					start: { String[] cmd ->
						Process process
						def commandLine = cmd.join(' ');
						switch(commandLine) {
							// result of the list command.
							// This list will grow as I get more knowledge about other devices available.
							case ~/.*-l/:
								process = ClewareControlTest.mockProcess(0,"""
									Cleware library version: 330
									Number of Cleware devices found: 3
                                    Device: 0, type: Switch1 (8), version: 106, serial number: 902492
                                    Device: 1, type: Switch1 (8), version: 106, serial number: 902493
                                    Device: 2, type: Unknown, version: 106, serial number: 902494
								""")
								break;
							// result of the status call to the first traffic light. // This one is working normally
							case ~/.*-c 1 -d 902492 -rs 0 -rs 1 -rs 2/:
								process = ClewareControlTest.mockProcess(0,"""
									Status: Off (0)
									Status: Off (0)
									Status: On (1)
								""")
								break;
							case ~/.*-c 1 -d 123456 -rs 0 -rs 1 -rs 2/:
								process = ClewareControlTest.mockProcess(1,"""
									Device 123456 not found
									errno at that time: Bad file descriptor (9)
								""")
								break;
							case ~/.*-c 1 -d 902492 -as .*/:
								process = ClewareControlTest.mockProcess(0,"""
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
			processStarter
		}
	}

  	protected MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	void initClewareControlAppTests() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void contextLoads() {
	}
}
