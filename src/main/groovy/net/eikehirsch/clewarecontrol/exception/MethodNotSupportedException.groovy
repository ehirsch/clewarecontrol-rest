package net.eikehirsch.clewarecontrol.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * @author Eike Hirsch
 * Date: 25.11.15
 * Time: 19:14
 */
@ResponseStatus(value=HttpStatus.METHOD_NOT_ALLOWED, reason="This method is not supported")
class MethodNotSupportedException extends RuntimeException {

	MethodNotSupportedException(String var1) {
		super(var1)
	}
}
