package com.example.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.Services.MapServiceBuilderImpl;

@RestController
public class WheelShareController {

	@Autowired
	private MapServiceBuilderImpl mapService;

	@GetMapping("/nodeMap")
	public String retrieveNodeMap() {
		return mapService.getNodeMap().toString();
	}

	@GetMapping("/edgeMap")
	public String retrieveEdgeMap() {
		return mapService.getEdgeMap().toString();
	}

	@GetMapping("/health")
	public String healthCheck() {
		return "Health: 100%! The backend server is up and running!";
	}

}