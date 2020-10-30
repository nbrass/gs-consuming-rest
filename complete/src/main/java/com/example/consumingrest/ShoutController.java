package com.example.consumingrest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ShoutController {

	@GetMapping("/shout")
	public Greeting shout(@RequestParam(value = "name", defaultValue = "World") String name) {
		RestTemplate restTemplate = new RestTemplate();
		HelloResponse helloResp = restTemplate.getForObject(
				"http://localhost:8080/greeting?name="+name, HelloResponse.class);
		return new Greeting(helloResp.getId(), helloResp.getContent().toUpperCase());
	}
}
