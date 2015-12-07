package net.eikehirsch.clewarecontrol.controller
import net.eikehirsch.clewarecontrol.ClewareControl
import net.eikehirsch.clewarecontrol.resource.UsageInfoResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
/**
 * @author Eike Hirsch
 * Date: 28.09.15
 * Time: 17:36
 */
@RestController
class IndexController {

  @Autowired
  private ClewareControl clewareControl;

  @RequestMapping(value="/", produces = "text/plain")
  String index() {
	"""this is going to be a short instruction on how to use this api."""
  }

  @RequestMapping(value="/", produces = "application/hal+json")
  UsageInfoResource info() {
	  UsageInfoResource info = new UsageInfoResource(title: "ClewareControl REST interface",
	                                                 text: "this is going to be a short instruction on how to use this api.")
	  info.add(ControllerLinkBuilder.linkTo (IndexController).withSelfRel())
	  info.add(ControllerLinkBuilder.linkTo (TrafficLightsController).withRel('trafficLights'))
	  info
  }

}
