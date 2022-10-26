package com.example.springboot;

import com.example.springboot.Models.MapNode;
import com.example.springboot.Services.RouteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.springboot.Services.MapServiceBuilderImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class WheelShareController {

	@Autowired
	private MapServiceBuilderImpl mapService;

	@Autowired
	private RouteServiceImpl routeService;

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

	@PostMapping("/algorithm")
	public List<MapNode> routeAlgo(@RequestParam float srcLon, @RequestParam float srcLat,
								   @RequestParam float destLon, @RequestParam float destLat) {
		// prepare
		Map<Long, MapNode> refToNode = mapService.getNodeMap();
		Map<Long, List<Long>> adj = mapService.getEdgeMap();
		// get the result
		List<Long> refNodes = routeService.getRoute(srcLon, srcLat, destLon, destLat, refToNode, adj);
		// convert
		List<MapNode> res = new ArrayList<>();
		for (Long ref : refNodes) {
			res.add(refToNode.get(ref));
		}
		// return
		return res;
	}


}