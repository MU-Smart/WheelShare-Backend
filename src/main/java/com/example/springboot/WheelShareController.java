package com.example.springboot;

import com.example.springboot.Models.MapNode;
import com.example.springboot.Services.RouteServiceImpl;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.springboot.Services.MapServiceBuilderImpl;

import java.util.ArrayList;
import java.util.HashMap;
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

	@PostMapping("/getRoute")
	public List<MapNode> retrieveRoute(@RequestParam float srcLon, @RequestParam float srcLat,
								   @RequestParam float destLon, @RequestParam float destLat) {

		Map<Long, MapNode> nodeMap = mapService.getNodeMap();
		Map<Long, List<Long>> edgeMap = mapService.getEdgeMap();
		// TODO: Implement the function to generate the weightMap
		Map<Pair<Long, Long>, Double> weightMap = new HashMap<>();
		// get the result
		List<Long> nodeIdRouteList = routeService.buildRoute(srcLon, srcLat, destLon, destLat, nodeMap, edgeMap, weightMap);
		// convert
		List<MapNode> result = new ArrayList<>();
		for (Long nodeId : nodeIdRouteList) {
			result.add(nodeMap.get(nodeId));
		}
		// return
		return result;
	}

	@GetMapping("/getClosestNode")
	@ResponseBody
	public Long retrieveClosestNode(@RequestParam float srcLon, @RequestParam float srcLat) {
		return routeService.getClosestNode(srcLon, srcLat, mapService.getNodeMap());
		// return srcLat;
	}


}