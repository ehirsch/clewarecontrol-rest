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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = ClewareControlApp)
@WebAppConfiguration
class ClewareControlAppTests {

	@Configuration
	static class ClewareControlMockingTestConfig {

		// Stub the runtime.
		@Bean
		@Primary
		ProcessStarter stubedProcessStarter() {
			def processStarter = [
					start: { String[] cmd ->
						def process
						def commandLine = cmd.join(' ');
						switch(commandLine) {
							case ~/.*-l/:
								process = ClewareControlTest.mockProcess(0,"""
									Cleware library version: 330
									Number of Cleware devices found: 2
                                    Device: 0, type: Switch1 (8), version: 106, serial number: 902492
                                    Device: 1, type: Switch1 (8), version: 106, serial number: 902493
								""")
						}
						process
					}
			] as ProcessStarter
			processStarter
		}
	}

  	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	void initClewareControlAppTests() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void contextLoads() {
	}

  	@Test
	void testList() {
		mockMvc.perform(get("/")).andDo(print())
	}
}
