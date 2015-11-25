package net.eikehirsch.clewarecontrol

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
/**
 * @author Eike Hirsch
 * Date: 28.09.15
 * Time: 17:36
 */
@RestController
class ClewareController {

  private static boolean DEBUG = true;

  @Autowired
  private ClewareControl clewareControl;

  @RequestMapping(value="/", produces = "text/plain")
  String index() {
	"""this is going to be a short instruction on how to use this api."""
  }

  @RequestMapping(value="/", produces = "application/hal+json")
  String list() {
	clewareControl.list();
	return ""
  }

  @RequestMapping("/as/{color}/{on}")
  String trafficLights(@PathVariable int color, @PathVariable int on) {

	  def cmd = ["clewarecontrol", "-as", "$color", "$on"]

	String output = run(cmd)
	return output;
  }


  private run(ArrayList<String> cmd) {
	// only echo the command if we are in debug mode
	if (DEBUG) {
	  cmd.putAt(0, "echo");
	}
	def process = cmd.execute()
	process.waitFor()

	def output = process.text
	println output
	output
  }

}
