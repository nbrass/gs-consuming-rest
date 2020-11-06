package com.example.consumingrest;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nbrass
 *
 *         Start application for local testing with following command: ./mvnw
 *         -Dspring-boot.run.arguments=--greetings.hostname=localhost
 *         spring-boot:run
 */
@Slf4j
@RestController
public class ShoutController {
	
	@Bean
	public RestTemplate template() {
	    return new RestTemplate();
	}
	
	@Autowired
	private RestTemplate template;

	@Value("${greetings.hostname:greeting}")
	private String greetingsHostname;

	@Value("${greetings.port:8080}")
	private Integer greetingsPort;

	@GetMapping("/shout")
	public Greeting shout(@RequestParam(value = "name", defaultValue = "World") String name,
			@RequestParam(value = "calls", defaultValue = "1") Integer calls,
			@RequestParam(value = "wait", defaultValue = "0") Integer wait) {
		template.setRequestFactory(new HttpComponentsClientHttpRequestFactory()); // needed for HTTP logging via
																						// Apache HttpClient

		// just some checks to avoid overheated CPUs
		if (wait < 0)
			return new Greeting(0, "I can't greet faster with a negative wait...");
		else if (wait > 5)
			return new Greeting(0, "Don't want to wait longer than 5 seconds...");

		if (calls < 0 || calls > 10)
			return new Greeting(0, "No, I'm not going to greet that often or less...");

		HelloResponse helloResp = new HelloResponse();
		for (int i = 1; i <= calls; i++) {
			log.info("### Call greeting service - " + i);
			helloResp = template.getForObject(
					"http://" + greetingsHostname + ":" + greetingsPort + "/greeting?name=" + name,
					HelloResponse.class);

			// wait a little bit
			try {
				TimeUnit.SECONDS.sleep(wait);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}
		return new Greeting(helloResp.getId(), helloResp.getContent().toUpperCase());
	}
}
