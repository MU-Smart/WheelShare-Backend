package com.example.springboot;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.Services.MapServiceImpl;

@RestController
public class HelloController {

	@Autowired
	private MapServiceImpl mapService;

	@GetMapping("/test")
	public String index() {
		String result = mapService.getNodeMap().toString();
		return result;
	}

	@GetMapping("/hello")
	public String index2() {
		return "Greetings from Spring Boot! 2";
	}

}