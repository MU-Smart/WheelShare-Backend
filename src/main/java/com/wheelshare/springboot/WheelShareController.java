package com.wheelshare.springboot;

import java.util.Arrays;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.wheelshare.springboot.Models.Errors;
import com.wheelshare.springboot.Models.MapNode;
import com.wheelshare.springboot.Models.MapRoute;
import com.wheelshare.springboot.Models.User;
import com.wheelshare.springboot.Services.MapServiceBuilderImpl;
import com.wheelshare.springboot.Services.RouteServiceImpl;
import com.wheelshare.springboot.Services.UserServiceImpl;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class WheelShareController {

	@Autowired
	private MapServiceBuilderImpl mapService;

	@Autowired
	private RouteServiceImpl routeService;

	@Autowired
	private UserServiceImpl userService;

	// ----------------------- Map Endpoint -------------------------
	@GetMapping("/webapi/health")
	public String healthCheck() {
		return "Health: 100%! The backend server is up and running!";
	}

	@GetMapping("/webapi/nodeMap")
	public String getNodeMap() {
		return mapService.getNodeMap().toString();
	}

	@GetMapping("/webapi/edgeMap")
	public String getEdgeMap() {
		return mapService.getEdgeMap().toString();
	}

	@GetMapping("/webapi/weightMap")
	public String getWeightMap() {
		return mapService.getWeightMap().toString();
	}

	@GetMapping("/webapi/getNodeById")
	public MapNode getNodeById(@RequestParam long nodeId) {
		if (!mapService.getNodeMap().containsKey(nodeId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.NODE_NOT_FOUND.getMessage());
		}
		return mapService.getNodeMap().get(nodeId);
	}

	@GetMapping("/webapi/getClosestNode")
	@ResponseBody
	public MapNode getClosestNode(@RequestParam double srcLat, @RequestParam double srcLon) {
		Long nodeId = routeService.getClosestNode(srcLat, srcLon, mapService.getNodeMap(), mapService.getEdgeMap());
		return mapService.getNodeMap().get(nodeId);
	}

	@GetMapping("/webapi/getNodeNeighborsById")
	public List<Long> getNodeNeighborsById(@RequestParam long nodeId) {
		if (!mapService.getEdgeMap().containsKey(nodeId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.NODE_NOT_FOUND.getMessage());
		}

		return mapService.getEdgeMap().get(nodeId);
	}

	@GetMapping("/webapi/getNodeNeighborsByCoor")
	@ResponseBody
	public List<Long> getNodeNeighborsByCoor(@RequestParam double srcLat, @RequestParam double srcLon) {
		Long nodeId = routeService.getClosestNode(srcLat, srcLon, mapService.getNodeMap(), mapService.getEdgeMap());
		return getNodeNeighborsById(nodeId);
	}

	@GetMapping("/webapi/getEdgeWeightById")
	@ResponseBody
	public Double getEdgeWeightById(@RequestParam long startNodeId, @RequestParam long endNodeId) {
		Pair<Long, Long> nodePair = new Pair<Long, Long>(startNodeId, endNodeId);

		if (!mapService.getWeightMap().containsKey(nodePair)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.NODE_PAIR_NOT_FOUND.getMessage());
		}
		return mapService.getWeightMap().get(nodePair);
	}

	@GetMapping("/webapi/getEdgeWeightByCoor")
	@ResponseBody
	public Double getEdgeWeightByCoor(@RequestParam double srcLat, @RequestParam double srcLon,
			@RequestParam double desLat,
			@RequestParam double desLon) {
		Long startNodeId = routeService.getClosestNode(srcLat, srcLon, mapService.getNodeMap(), mapService.getEdgeMap());
		Long endNodeId = routeService.getClosestNode(desLat, desLon, mapService.getNodeMap(), mapService.getEdgeMap());

		return getEdgeWeightById(startNodeId, endNodeId);
	}

	@GetMapping("/webapi/getSingleRoute")
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

	@GetMapping("/webapi/getMultipleRoute")
	@ResponseBody
	public List<MapRoute> getMultipleRoute(@RequestParam double srcLat, @RequestParam double srcLon,
			@RequestParam double destLat, @RequestParam double destLon, @RequestParam double radiusCoefficent)
			throws InvalidAlgorithmParameterException {
		Map<Long, MapNode> nodeMap = mapService.getNodeMap();
		Map<Long, List<Long>> edgeMap = mapService.getEdgeMap();
		Map<Pair<Long, Long>, Double> weightMap = mapService.getWeightMap();

		// * Route building
		List<List<Long>> nodeIdRouteList = routeService.buildMultipleRoute(srcLat, srcLon, destLat, destLon,
				radiusCoefficent, nodeMap,
				edgeMap);

		List<MapRoute> result = new ArrayList<>();

		for (List<Long> nodeIdPath : nodeIdRouteList) {
			result.add(routeService.routeStatisticGeneration(nodeIdPath, nodeMap, weightMap));
		}

		return result;
	}

	// ----------------------- Users Endpoint -------------------------
	@GetMapping("/webapi/createUser")
	@ResponseBody
	public String createUser(@RequestParam String email, @RequestParam String password, @RequestParam String name,
			@RequestParam int age, @RequestParam String gender, @RequestParam double height, @RequestParam double weight,
			@RequestParam String type_wc, @RequestParam String wheel_type, @RequestParam String tire_mat,
			@RequestParam double wc_height,
			@RequestParam double wc_width) throws InterruptedException, ExecutionException {
		return userService.createUser(email, password, name, age, gender, height, weight, type_wc, wheel_type, tire_mat,
				wc_height, wc_width);
	}

	@GetMapping("/webapi/retrieveUserByEmail")
	@ResponseBody
	public User retrieveUserByEmail(@RequestParam String email) throws InterruptedException, ExecutionException {
		return userService.retrieveUserByEmail(email);
	}

	@GetMapping("/webapi/retrieveUserIdByEmail")
	@ResponseBody
	public String retrieveUserIdByEmail(@RequestParam String email) throws InterruptedException, ExecutionException {
		return userService.retrieveUserIdByEmail(email);
	}

	@GetMapping("/webapi/updateUserByEmail")
	@ResponseBody
	public String updateUserByEmail(@RequestParam String oldEmail, @RequestParam String newEmail,
			@RequestParam String password,
			@RequestParam String name, @RequestParam int age, @RequestParam String gender, @RequestParam double height,
			@RequestParam double weight,
			@RequestParam String type_wc, @RequestParam String wheel_type, @RequestParam String tire_mat,
			@RequestParam double wc_height,
			@RequestParam double wc_width) throws InterruptedException, ExecutionException {
		return userService.updateUserByEmail(oldEmail, newEmail, password, name, age, gender, height, weight, type_wc,
				wheel_type, tire_mat, wc_height, wc_width);
	}

	@GetMapping("/webapi/deleteUserByEmail")
	@ResponseBody
	public String deleteUserByEmail(@RequestParam String email) throws InterruptedException, ExecutionException {
		return userService.deleteUserByEmail(email);
	}

	// ----------------------- Testing Endpoint -------------------------
	@GetMapping("/webapi/testRoute")
	@ResponseBody
	public MapRoute testRoute() throws InvalidAlgorithmParameterException {
		Map<Long, MapNode> nodeMap = mapService.getNodeMap();
		Map<Pair<Long, Long>, Double> weightMap = mapService.getWeightMap();
		List<Long> testNodeIdList = new ArrayList<>(
				Arrays.asList(7213516557L, 7928535764L, 7928535763L, 5593551604L, 5594886945L, 10187258366L, 7213516553L));

		return routeService.routeStatisticGeneration(testNodeIdList, nodeMap, weightMap);
	}
}