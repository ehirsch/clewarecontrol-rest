package net.eikehirsch.clewarecontrol.controller
import net.eikehirsch.clewarecontrol.ClewareControlAppTests
import org.junit.Test

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
/**
 * This will test the IndexController against the text fixture defined in {@link ClewareControlAppTests}.
 */
public class IndexControllerTest extends ClewareControlAppTests {
	
	@Test
	public void testIndex() throws Exception {
		mockMvc.perform(get("/").accept("text/plain"))
				.andExpect(content().contentTypeCompatibleWith("text/plain"))
	}

	@Test
	void infoShouldProvideSomeInformationAndLinks() {
		mockMvc.perform(get("/").accept("application/hal+json"))
//				.andDo(print())
				.andExpect(content().contentTypeCompatibleWith("application/hal+json"))
				.andExpect(jsonPath('title').exists())
				.andExpect(jsonPath('text').exists())
				.andExpect(jsonPath('_links').exists())
	}

	@Test
	void infoContainsLinksToSelfAndTrafficLights() {
		mockMvc.perform(get("/").accept("application/hal+json"))
//				.andDo(print())
				.andExpect(jsonPath('$._links.self').exists())
				.andExpect(jsonPath('$._links.trafficLights').exists())
	}

}