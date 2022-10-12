package com.example.springboot;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.springboot.ScheduledTasks;

@RestController
public class HelloController {

	@Autowired
	private ScheduledTasks scheduledTasks;

	@GetMapping("/test")
	public String index() {
		String result = scheduledTasks.result;
		return result;
	}

	@GetMapping("/hello")
	public String index2() {
		return "Greetings from Spring Boot! 2";
	}

}