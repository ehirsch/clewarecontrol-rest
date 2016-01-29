package net.eikehirsch.clewarecontrol.controller
import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import net.eikehirsch.clewarecontrol.ClewareControlAppTests
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

import static org.hamcrest.Matchers.containsInAnyOrder
import static org.hamcrest.Matchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
/**
 * This will test the IndexController against the text fixture defined in {@link ClewareControlAppTests}.
 */
@Slf4j
class TrafficLightsControllerTest extends ClewareControlAppTests {

	private json

	@Before
	public void setUp ( ) throws Exception {
		json = new JsonBuilder()
	}

	@Test
	public void testIndex() throws Exception {
		mockMvc.perform(get("/trafficLights").accept("text/plain"))
				.andExpect(content().contentTypeCompatibleWith("text/plain"))
	}

	@Test
	void infoShouldProvideSomeInformationAndLinks() {
		mockMvc.perform(get("/trafficLights"))
				.andExpect(content().contentTypeCompatibleWith("application/*+json"))
				.andExpect(jsonPath('title').exists())
				.andExpect(jsonPath('text').exists())
				.andExpect(jsonPath('_links').exists())
	}

	@Test
	void infoShouldProvideSomeInformationAndLinks_trailingSlash() {
		mockMvc.perform(get("/trafficLights/"))
				.andExpect(content().contentTypeCompatibleWith("application/*+json"))
				.andExpect(jsonPath('title').exists())
				.andExpect(jsonPath('text').exists())
				.andExpect(jsonPath('_links').exists())
	}

	@Test
	@Ignore
	void infoEmbedsLinksToAllTrafficLights() {
		mockMvc.perform(get("/trafficLights"))
				.andExpect(jsonPath('$._embedded').exists())
				.andExpect(jsonPath('$._embedded.trafficLights').exists())
				.andExpect(jsonPath('$._embedded.trafficLights[*].id', containsInAnyOrder(902492,902493)))
	}

	// API
	// The test setup created two traffic light devices. For our tests we simply use the first one.
	// id is : 902492
	// GET -> {r:0|1,y:0|1,g:0|1}
	@Test
	void shouldReturnStatusOfAllLightsWhenASingleDeviceIsRequested() {
		// requesting a single device means: GET + id
		mockMvc.perform(get("/trafficLights/902492"))
				.andExpect(jsonPath('$.id', equalTo(902492)))
				.andExpect(jsonPath('$.r', equalTo(false)))
				.andExpect(jsonPath('$.y', equalTo(false)))
				.andExpect(jsonPath('$.g', equalTo(true)))
	}

	// -- GET with invalid ids
	@Test
	void shouldReturnNotFoundForUnknownIdOnGet() {
		// requesting a single device means: GET + id
		mockMvc.perform(get("/trafficLights/123456"))
				.andExpect(status().is(404))
	}

	@Test
	void shouldReturnBadRequestForInvalidIdOnGet() {
		// requesting a single device means: GET + id
		mockMvc.perform(get("/trafficLights/abcdef"))
				.andExpect(status().is(400))
	}

	// GET TODO: HAL

	// -- PUT + {r:0|1,y:0|1,g:0|1} -> 202
	@Test
	void shouldReturnAcceptedWhenACompleteTrafficLightsSetIsPushed() {
		json {
			r true
			y false
			g false
		}
		mockMvc.perform(put("/trafficLights/{id}", 902492)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(json.toString()))
				.andExpect(status().is(202)) // accepted
	}

	// -- PUT + {g:0|1} -> 202
	@Test
	void shouldReturnAcceptedWhenPartsArePushed() {
		json {
			g true
		}
		mockMvc.perform(put("/trafficLights/{id}", 902492)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(this.json.toString()))
				.andExpect(status().is(202)) // accepted
	}

	// -- PUT invalid
	@Test
	void shouldReturnBadRequestWhenBodyIsMissing() {
		mockMvc.perform(put("/trafficLights/{id}", 902492)
				                .contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(400)) // bad request
	}

	@Test
	void shouldReturnBadRequestWhenInvalidIdIsPassed() {
		json {
			g true
		}
		mockMvc.perform(put("/trafficLights/{id}", 'abc')
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(this.json.toString()))
				.andExpect(status().is(400))
	}

	@Test
	void shouldReturnNotFoundWhenUnknownIdIsPassed() {
		json {
			g true
		}
		mockMvc.perform(put("/trafficLights/{id}", 123456)
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(this.json.toString()))
				.andExpect(status().is(404))
	}

	// -- unsupported methods
	// POST -> 405 "Method not allowed" + HEADER "Allowed" GET, PUT
	@Test
	void shouldReturnMethodNotAllowedForPostRequests() {
		mockMvc.perform(post("/trafficLights")
				                .contentType(MediaType.APPLICATION_JSON))
		.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is(405))
	}

	@Test
	void shouldReturnMethodNotAllowedForPostRequestsWithAnyPayload() {
		json {
			g true
		}

		mockMvc.perform(post("/trafficLights/123")
				                .contentType(MediaType.APPLICATION_JSON)
				                .content(this.json.toString()))
		.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is(405))
	}

	@Test
	void shouldReturnMethodNotAllowedForDeleteRequests() {
		mockMvc.perform(delete("/trafficLights").contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is(405))
	}

	@Test
	void shouldReturnMethodNotAllowedForPatchRequests() {
		mockMvc.perform(patch("/trafficLights").contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is(405))
	}

	@Test
	void shouldReturnMethodNotAllowedForHeadRequests() {
		mockMvc.perform(head("/trafficLights").contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().is(405))
	}

}