package net.eikehirsch.clewarecontrol

import net.eikehirsch.clewarecontrol.process.ProcessBuilderProcessStarter
import net.eikehirsch.clewarecontrol.process.ProcessStarter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ClewareControlApp {

    static void main(String[] args) {
        SpringApplication.run ClewareControlApp, args
    }

	@Bean
	ProcessStarter processBuilderProcessStarter() {
	  return new ProcessBuilderProcessStarter()
  	}

  	@Autowired
	@Bean
    ClewareControl clewareControl( ProcessStarter processStarter) {
	  return new ClewareControl(processStarter)
	}


}

// clewarecontrol -c 1 -d 902492 -as 2 0


// clewarecontrol -l
// Device: 0, type: Switch1 (8), version: 106, serial number: 902492

// no binary
// java.io.IOException "Cannot run program "clewarecontrol": error=2, No such file or directory"

