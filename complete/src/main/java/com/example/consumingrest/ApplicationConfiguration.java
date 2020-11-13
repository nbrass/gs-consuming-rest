package com.example.consumingrest;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfiguration {
	@Bean
	public HttpTraceRepository htttpTraceRepository()
	{
	  return new InMemoryHttpTraceRepository();
	}
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		// needed for HTTP logging via Apache HttpClient
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory()); 
	    return restTemplate;
	}
}