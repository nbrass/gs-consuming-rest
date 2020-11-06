package com.example.consumingrest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


/**
 * @author nbrass
 *
 * Start application for local testing with following command:
 * 		./mvnw -Dspring-boot.run.arguments=--greetings.hostname=localhost spring-boot:run
 */
@RestController
public class ShoutController {

	@Value("${greetings.hostname:greeting}")
	private String greetingsHostname;
	
	@Value("${greetings.port:8080}")
	private Integer greetingsPort;

	@GetMapping("/shout")
	public Greeting shout(@RequestParam(value = "name", defaultValue = "World") String name) {
		RestTemplate restTemplate = new RestTemplate();
		HelloResponse helloResp = restTemplate.getForObject(
				"http://"+ greetingsHostname +":"+ greetingsPort + "/greeting?name="+name, HelloResponse.class);
		return new Greeting(helloResp.getId(), helloResp.getContent().toUpperCase());
	}
}
