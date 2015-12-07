package net.eikehirsch.clewarecontrol.controller
import net.eikehirsch.clewarecontrol.ClewareControl
import net.eikehirsch.clewarecontrol.device.TrafficLightsDevice
import net.eikehirsch.clewarecontrol.resource.UsageInfoResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ExposesResourceFor(TrafficLightsDevice)
@RequestMapping("/trafficLights")
class TrafficLightsController {

  @Autowired
  private ClewareControl clewareControl;

  @RequestMapping(produces = "text/plain")
  String index() {
	"""this is going to be a short instruction on how to use this controller"""
  }

  @RequestMapping(produces = "application/hal+json")
  UsageInfoResource info() {


	  UsageInfoResource info = new UsageInfoResource(title: "ClewareControl traffic lights",
	                                                 text: "this is going to be a short instruction on how to use this api.")
	  info.add(ControllerLinkBuilder.linkTo (TrafficLightsController).withSelfRel())
	  info
  }

}
