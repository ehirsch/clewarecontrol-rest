package net.eikehirsch.clewarecontrol.trafficlights

import net.eikehirsch.clewarecontrol.ClewareControl

import net.eikehirsch.clewarecontrol.usage.IndexController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

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
  ResponseEntity<IndexController.UsageInfoResource> info() {

	  IndexController.UsageInfoResource info = new IndexController.UsageInfoResource(title: "ClewareControl traffic lights",
	                                                                                 text: "this is going to be a short instruction on how to use this api.")
	  info.add(ControllerLinkBuilder.linkTo (TrafficLightsController).withSelfRel())
	  new ResponseEntity(info, HttpStatus.OK)
  }

}
