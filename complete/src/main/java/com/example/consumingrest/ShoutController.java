package com.example.consumingrest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import brave.Tracer;
import brave.baggage.BaggageField;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nbrass
 *
 */
@Slf4j
@RestController
public class ShoutController {

	@Autowired
	private Tracer tracer;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${greetings.hostname:greeting}")
	private String greetingsHostname;

	@Value("${greetings.port:8080}")
	private Integer greetingsPort;

	@GetMapping("/shout")
	public Greeting shout(@RequestParam(value = "name", defaultValue = "World") String name,
			@RequestParam(value = "calls", defaultValue = "1") Integer calls,
			@RequestParam(value = "wait", defaultValue = "0") Integer wait) {

		// just some checks to avoid overheated CPUs
		if (wait < 0)
			return new Greeting(0, "I can't greet faster with a negative wait...");
		else if (wait > 5)
			return new Greeting(0, "Don't want to wait longer than 5 seconds...");

		if (calls < 0 || calls > 10)
			return new Greeting(0, "No, I'm not going to greet that often or less...");

		HelloResponse helloResp = new HelloResponse();
		for (int i = 1; i <= calls; i++) {

			// here comes our custom header that will be added as HTTP-Header
			// -> the key needs to be added in application.yaml
			// (spring.sleuth.baggage.remote-fields)
			BaggageField requestBaggage = BaggageField.create("requestId");
			requestBaggage.updateValue(tracer.currentSpan().context().traceIdString());
			
			BaggageField machineBaggage = BaggageField.create("machineName");
			machineBaggage.updateValue(getHostname());

			log.info("### Call greeting service - " + i);
			helloResp = restTemplate.getForObject(
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

	private String getHostname() {
		String hostname = "Unknown";

		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
		} catch (UnknownHostException ex) {
			System.out.println("Hostname can not be resolved");
		}
		return hostname;
	}
}
