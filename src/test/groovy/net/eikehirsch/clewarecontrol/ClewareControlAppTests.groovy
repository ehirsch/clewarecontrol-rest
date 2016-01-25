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
			StubUtils.processStarterStub
		}
	}

  	protected MockMvc mockMvc;

	@Autowired
	private        WebApplicationContext                   webApplicationContext

	@Before
	void initClewareControlAppTests() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void contextLoads() {
	}
}
