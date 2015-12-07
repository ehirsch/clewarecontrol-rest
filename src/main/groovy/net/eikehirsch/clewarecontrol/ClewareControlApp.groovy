package net.eikehirsch.clewarecontrol

import net.eikehirsch.clewarecontrol.process.ProcessBuilderProcessStarter
import net.eikehirsch.clewarecontrol.process.ProcessStarter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.hateoas.config.EnableHypermediaSupport

@SpringBootApplication
@EnableHypermediaSupport( type = EnableHypermediaSupport.HypermediaType.HAL)
class ClewareControlApp {

    static void main(String[] args) {
        SpringApplication.run ClewareControlApp, args
    }

	@Bean
	ProcessStarter processBuilderProcessStarter() {
	  return new ProcessBuilderProcessStarter()
  	}

	@Bean
    ClewareControl clewareControl( ProcessStarter processStarter) {
	  return new ClewareControl(processStarter)
	}


}
