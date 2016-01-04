package net.eikehirsch.clewarecontrol.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * @author Eike Hirsch
 * Date: 25.11.15
 * Time: 19:14
 */
@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Device")
class DeviceNotFoundException extends Exception {

	DeviceNotFoundException(String var1) {
		super(var1)
	}
}
