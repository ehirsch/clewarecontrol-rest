package net.eikehirsch.clewarecontrol.controller

import jdk.nashorn.internal.ir.annotations.Ignore
import net.eikehirsch.clewarecontrol.ClewareControlAppTests
import org.junit.Test

import static org.hamcrest.Matchers.containsInAnyOrder
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
/**
 * This will test the IndexController against the text fixture defined in {@link ClewareControlAppTests}.
 */
class TrafficLightsControllerTest extends ClewareControlAppTests {
	
	@Test
	public void testIndex() throws Exception {
		mockMvc.perform(get("/trafficLights").accept("text/plain"))
				.andExpect(content().contentTypeCompatibleWith("text/plain"))
	}

	@Test
	void infoShouldProvideSomeInformationAndLinks() {
		mockMvc.perform(get("/trafficLights").accept("application/hal+json"))
				.andExpect(content().contentTypeCompatibleWith("application/hal+json"))
				.andExpect(jsonPath('title').exists())
				.andExpect(jsonPath('text').exists())
				.andExpect(jsonPath('_links').exists())
	}

	@Test
	void infoShouldProvideSomeInformationAndLinks_trailingSlash() {
		mockMvc.perform(get("/trafficLights/").accept("application/hal+json"))
				.andExpect(content().contentTypeCompatibleWith("application/hal+json"))
				.andExpect(jsonPath('title').exists())
				.andExpect(jsonPath('text').exists())
				.andExpect(jsonPath('_links').exists())
	}

	@Test
	@Ignore
	void infoEmbedsLinksAllTrafficLights() {
		mockMvc.perform(get("/trafficLights").accept("application/hal+json"))
				.andDo(print())
				.andExpect(jsonPath('$._embedded').exists())
				.andExpect(jsonPath('$._embedded.trafficLights').exists())
				.andExpect(jsonPath('$._embedded.trafficLights[*].id', containsInAnyOrder(902492,902493))) // TODO: check each light
	}

	// clewarecontrol -c 1 -d 902492 -as 2 0

}