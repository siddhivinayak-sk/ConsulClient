package com.consul.test.controllers;

import java.net.URI;
import java.util.Optional;

import javax.naming.ServiceUnavailableException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.consul.test.config.MyProperties;

@RestController
public class RestHomeController {

	@Autowired
    private DiscoveryClient discoveryClient;

	@Autowired
    private RestTemplate restTemplate;

	
	@RequestMapping("/")
	public String hello() {
		return "Hello";
	}

	@RequestMapping("/service")
    public Optional<URI> serviceUrl() {
        return discoveryClient.getInstances("myApp")
          .stream()
          .map(si -> si.getUri())
          .findFirst();
    }	

	@GetMapping("/discoveryClient")
	public String discoveryPing() throws RestClientException, 
	  ServiceUnavailableException {
	    URI service = serviceUrl()
	      .map(s -> s.resolve("/ping"))
	      .orElseThrow(ServiceUnavailableException::new);
	    return restTemplate.getForEntity(service, String.class)
	      .getBody();
	}
	 
	@GetMapping("/ping")
	public String ping() {
	    return "pong";
	}	
	
	@GetMapping("/my-health-check")
	public ResponseEntity<String> myCustomCheck() {
	    String message = "Testing my healh check function";
	    return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
	@Value("${my.prop}")
	private String prop;
	
	@Autowired
	private MyProperties myProperties;
	
	@RequestMapping("/prop1")
	public String getProp1() {
		return prop;
	}
	
	@RequestMapping("/prop2")
	public String getProp2() {
		return myProperties.getProp();
	}
}
