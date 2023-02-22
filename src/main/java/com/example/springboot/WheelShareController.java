package com.example.springboot;

import com.example.springboot.Models.MapNode;
import com.example.springboot.Models.MapRoute;
import com.example.springboot.Services.RouteServiceImpl;

import java.util.Arrays;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.springboot.Services.MapServiceBuilderImpl;

import java.security.InvalidAlgorithmParameterException;
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
	public String getNodeMap() {
		return mapService.getNodeMap().toString();
	}

	@GetMapping("/edgeMap")
	public String getEdgeMap() {
		return mapService.getEdgeMap().toString();
	}

	@GetMapping("/weightMap")
	public String getWeightMap() {
		return mapService.getWeightMap().toString();
	}

	@GetMapping("/health")
	public String healthCheck() {
		return "Health: 100%! The backend server is up and running!";
	}

	@GetMapping("/getSingleRoute")
	@ResponseBody
	public MapRoute getSingleRoute(@RequestParam double srcLat, @RequestParam double srcLon,
			@RequestParam double destLat, @RequestParam double destLon) throws InvalidAlgorithmParameterException {

		Map<Long, MapNode> nodeMap = mapService.getNodeMap();
		Map<Long, List<Long>> edgeMap = mapService.getEdgeMap();
		Map<Pair<Long, Long>, Double> weightMap = mapService.getWeightMap();

		// * Route building
		List<Long> nodeIdRouteList = routeService.buildSingleRoute(srcLat, srcLon, destLat, destLon, nodeMap, edgeMap,
				weightMap);

		return routeService.routeStatisticGeneration(nodeIdRouteList, nodeMap, weightMap);
	}

	@GetMapping("/getMultipleRoute")
	@ResponseBody
	public List<MapRoute> getMultipleRoute(@RequestParam double srcLat, @RequestParam double srcLon,
			@RequestParam double destLat, @RequestParam double destLon, @RequestParam double radiusCoefficent) throws InvalidAlgorithmParameterException {
		Map<Long, MapNode> nodeMap = mapService.getNodeMap();
		Map<Long, List<Long>> edgeMap = mapService.getEdgeMap();
		Map<Pair<Long, Long>, Double> weightMap = mapService.getWeightMap();

		// * Route building
		List<List<Long>> nodeIdRouteList = routeService.buildMultipleRoute(srcLat, srcLon, destLat, destLon, radiusCoefficent, nodeMap,
				edgeMap, weightMap);

		List<MapRoute> result = new ArrayList<>();

		for (List<Long> nodeIdPath : nodeIdRouteList) {
			result.add(routeService.routeStatisticGeneration(nodeIdPath, nodeMap, weightMap));
		}

		return result;
	}

	@GetMapping("/getNodeNeighborsByCoor")
	@ResponseBody
	public List<Long> getNodeNeighborsByCoor(@RequestParam double srcLat, @RequestParam double srcLon) {
		Long nodeId = routeService.getClosestNode(srcLat, srcLon, mapService.getNodeMap());
		return mapService.getEdgeMap().get(nodeId);
	}

	@GetMapping("/getEdgeWeight")
	@ResponseBody
	public Double getEdgeWeight(@RequestParam double srcLat, @RequestParam double srcLon, @RequestParam double desLat,
			@RequestParam double desLon) {
		Long startNodeId = routeService.getClosestNode(srcLat, srcLon, mapService.getNodeMap());
		Long endNodeId = routeService.getClosestNode(desLat, desLon, mapService.getNodeMap());

		Pair<Long, Long> nodePair = new Pair<Long, Long>(startNodeId, endNodeId);

		if (!mapService.getWeightMap().containsKey(nodePair)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Node pair not found");
		}
		return mapService.getWeightMap().get(nodePair);
	}

	@GetMapping("/getClosestNode")
	@ResponseBody
	public MapNode getClosestNode(@RequestParam double srcLat, @RequestParam double srcLon) {
		Long nodeId = routeService.getClosestNode(srcLat, srcLon, mapService.getNodeMap());
		return mapService.getNodeMap().get(nodeId);
	}

	@GetMapping("/testRoute")
	@ResponseBody
	public MapRoute testRoute() throws InvalidAlgorithmParameterException {
		Map<Long, MapNode> nodeMap = mapService.getNodeMap();
		Map<Pair<Long, Long>, Double> weightMap = mapService.getWeightMap();
		List<Long> testNodeIdList = new ArrayList<>(
				Arrays.asList(7213516557L, 7928535764L, 7928535763L, 5593551604L, 5594886945L, 10187258366L, 7213516553L));

		return routeService.routeStatisticGeneration(testNodeIdList, nodeMap, weightMap);
	}
}