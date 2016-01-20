package net.eikehirsch.clewarecontrol.controller
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import net.eikehirsch.clewarecontrol.ClewareControlAppTests
import org.junit.Ignore
import org.junit.Test
import org.springframework.http.MediaType

import static org.hamcrest.Matchers.containsInAnyOrder
import static org.hamcrest.Matchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
/**
 * This will test the IndexController against the text fixture defined in {@link ClewareControlAppTests}.
 */
@Slf4j
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
	void infoEmbedsLinksToAllTrafficLights() {
		mockMvc.perform(get("/trafficLights").accept("application/hal+json"))
				.andExpect(jsonPath('$._embedded').exists())
				.andExpect(jsonPath('$._embedded.trafficLights').exists())
				.andExpect(jsonPath('$._embedded.trafficLights[*].id', containsInAnyOrder(902492,902493)))
	}

	// clewarecontrol -c 1 -d 902492 -as 2 0

	// API
	// The test setup created two traffic light devices. For our tests we simply use the first one.
	// id is : 902492
	// GET -> {r:0|1,y:0|1,g:0|1}
	@Test
	void shouldReturnStatusOfAllLightsWhenASingleDeviceIsRequested() {
		// requesting a single device means: GET + id
		mockMvc.perform(get("/trafficLights/902492").accept("application/hal+json"))
				.andExpect(jsonPath('$.id', equalTo(902492)))
				.andExpect(jsonPath('$.r', equalTo(false)))
				.andExpect(jsonPath('$.y', equalTo(false)))
				.andExpect(jsonPath('$.g', equalTo(true)))
	}

	// GET with invalid ids
	@Test
	void shouldReturnNotFoundForUnknownIdOnGet() {
		// requesting a single device means: GET + id
		mockMvc.perform(get("/trafficLights/123456").accept("application/hal+json"))
				.andExpect(status().is(404))
	}

	@Test
	void shouldReturnBadRequestForInvalidIdOnGet() {
		// requesting a single device means: GET + id
		mockMvc.perform(get("/trafficLights/abcdef").accept("application/hal+json"))
				.andExpect(status().is(400))
	}

	// GET TODO: HAL

	// PUT + {r:0|1,y:0|1,g:0|1} -> 204 or 200?
	@Test
	void shouldReturnAcceptedWhen() {
		def json = new JsonBuilder()
		json {
			r true
			y false
			g false
		}
		mockMvc.perform(put("/trafficLights/{id}", 902492)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(json.toString())
				                .accept("application/hal+json"))
				.andDo(print())
				.andExpect(status().is(202)) // accepted
		// TODO: do we need to check the outcome?
	}

	// POST -> 405 "Method not allowed" + HEADER "Allowed" GET, PUT,
	// DELETE -> 405 "Method not allowed" + HEADER "Allowed" GET, PUT,
}