package net.eikehirsch.clewarecontrol.controller

import net.eikehirsch.clewarecontrol.ClewareControl
import net.eikehirsch.clewarecontrol.resource.UsageInfoResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author Eike Hirsch
 * Date: 28.09.15
 * Time: 17:36
 */
@Controller
@RequestMapping("/")
class IndexController {

  @Autowired
  private ClewareControl clewareControl;

  @RequestMapping(produces = "text/plain")
  ResponseEntity<String> index() {
	new ResponseEntity<String>("""this is going to be a short instruction on how to use this api.""", HttpStatus.OK)
  }

  @RequestMapping
  ResponseEntity<UsageInfoResource> info() {
	  UsageInfoResource info = new UsageInfoResource(title: "ClewareControl REST interface",
	                                                 text: "this is going to be a short instruction on how to use this api.")
	  info.add(ControllerLinkBuilder.linkTo (IndexController).withSelfRel())
	  info.add(ControllerLinkBuilder.linkTo (TrafficLightsController).withRel('trafficLights'))
	  new ResponseEntity<UsageInfoResource>(info, HttpStatus.OK)
  }

}
