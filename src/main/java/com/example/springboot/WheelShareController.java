package com.example.springboot;

import com.example.springboot.Models.MapNode;
import com.example.springboot.Services.RouteServiceImpl;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.springboot.Services.MapServiceBuilderImpl;

import java.util.ArrayList;
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

	@GetMapping("/weightMap")
	public String retrieveWeightMap() {
		return mapService.getWeightMap().toString();
	}

	@GetMapping("/health")
	public String healthCheck() {
		return "Health: 100%! The backend server is up and running!";
	}

	@GetMapping("/getRoute")
	@ResponseBody
	public List<MapNode> retrieveRoute(@RequestParam double srcLon, @RequestParam double srcLat,
			@RequestParam double destLon, @RequestParam double destLat) {

		Map<Long, MapNode> nodeMap = mapService.getNodeMap();
		Map<Long, List<Long>> edgeMap = mapService.getEdgeMap();
		Map<Pair<Long, Long>, Double> weightMap = mapService.getWeightMap();

		// * Route building
		List<Long> nodeIdRouteList = routeService.buildRoute(srcLon, srcLat, destLon, destLat, nodeMap, edgeMap, weightMap);

		// * Convert list of nodeId -> node
		List<MapNode> result = new ArrayList<>();
		for (Long nodeId : nodeIdRouteList) {
			result.add(nodeMap.get(nodeId));
		}

		return result;
	}

	@GetMapping("/getClosestNode")
	@ResponseBody
	public Long retrieveClosestNode(@RequestParam double srcLat, @RequestParam double srcLon) {
		return routeService.getClosestNode(srcLat, srcLon, mapService.getNodeMap());
	}

}