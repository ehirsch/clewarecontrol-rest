package net.eikehirsch.clewarecontrol.trafficlights
import groovy.util.logging.Slf4j
import net.eikehirsch.clewarecontrol.ClewareControl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Slf4j
@Controller
@ExposesResourceFor(TrafficLightsDevice)
@RequestMapping("/trafficLights")
class TrafficLightsController {

  @Autowired
  private ClewareControl clewareControl;

  @RequestMapping(produces = "text/plain")
  ResponseEntity index() {
	new ResponseEntity("""this is going to be a short instruction on how to use this controller""", HttpStatus.OK)
  }

  @RequestMapping
  ResponseEntity<TrafficLightsUsageInfoResource> info() {

	  TrafficLightsUsageInfoResource info = new TrafficLightsUsageInfoResource(
			  title: "ClewareControl traffic lights",
			  text: "this is going to be a short instruction on how to use this api.")
	  info.add(ControllerLinkBuilder.linkTo (TrafficLightsController).withSelfRel())
	  // TODO: create embedded information
	  def devices = clewareControl.listDevices(TrafficLightsDevice)
	  devices.each {
		  log.info "${it.id}"
	  }
	  new ResponseEntity(info, HttpStatus.OK)
  }

  @RequestMapping(value="/{id}", method = RequestMethod.GET)
  ResponseEntity<TrafficLightsDevice> get(@PathVariable int id) {
	  new ResponseEntity(clewareControl.createTrafficLightsDevice(id), HttpStatus.OK)
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  ResponseEntity<TrafficLightsDevice> put(@PathVariable int id, @RequestBody TrafficLightsDevice device) {

	  // no matter which if an id was transmitted in the body
	  // we are going to use the id from the url
	  device.id = id
	  // and now we are going to update the status.
	  clewareControl.updateTrafficLights device
	  new ResponseEntity(HttpStatus.ACCEPTED)
  }

	/**
	 * Handle unsupported requests
	 */
	@RequestMapping(method = [
			RequestMethod.POST,
			RequestMethod.DELETE,
			RequestMethod.PATCH,
			RequestMethod.HEAD
	])
	ResponseEntity unsupported() {
		def headers = new LinkedMultiValueMap<String, String>()
		headers.add('Allow', 'GET')
		headers.add('Allow', 'PUT')
		new ResponseEntity(headers, HttpStatus.METHOD_NOT_ALLOWED)
	}

}
