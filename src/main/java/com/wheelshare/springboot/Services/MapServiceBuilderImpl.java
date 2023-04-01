package com.wheelshare.springboot.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.wheelshare.springboot.Models.Errors;
import com.wheelshare.springboot.Models.MapNode;

import java.io.FileReader;

import org.javatuples.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Service
public class MapServiceBuilderImpl implements MapServiceBuilder {

	private static final Logger log = LoggerFactory.getLogger(MapServiceBuilderImpl.class);
	/**
	 * * nodeMap maps nodeId -> the real node with all of its related data
	 * * edgeMap maps nodeId -> the id of all of its neighboring node's id
	 * * weightMap maps a pair of node ids (or an edge) -> its weight
	 */
	private Map<Long, MapNode> nodeMap = new HashMap<Long, MapNode>();
	private Map<Long, List<Long>> edgeMap = new HashMap<Long, List<Long>>();
	private Map<Pair<Long, Long>, Double> weightMap = new HashMap<Pair<Long, Long>, Double>();
	private HashSet<Long> closedNodeSet = new HashSet<Long>();

	JSONParser parser = new JSONParser();

	// This function is executed every 5 minutes
	@Scheduled(fixedRate = 300000)
	public void buildMap() {
		try {
			String absoluteFilePath = new File("").getAbsolutePath();

			// * Read the file in
			// Object jsonFileObject = parser.parse(new FileReader(absoluteFilePath +
			// "/src/main/resources/mapData.json"));
			Object jsonFileObject = parser.parse(new FileReader(absoluteFilePath +
					"/wheel-share-backend/mapData.json"));
			JSONObject jsonObject = (JSONObject) jsonFileObject;

			// * Clean up all of the hashmaps to put new data in
			// ? Is there any way we can pull new data in
			// ? without having to clear the hashmaps each time?
			nodeMap.clear();
			edgeMap.clear();
			weightMap.clear();

			JSONArray jsonElementArray = (JSONArray) jsonObject.get("elements");
			int noTypeElements = 0; // a counter to count how many elems does not have a type
			int waysWithoutTag = 0; // a counter to count how many ways does not have a tag
			int waysWithInvalidIncline = 0; // a counter to count how many ways does not have a valid incline

			for (int i = 0; i < jsonElementArray.size(); i++) {
				JSONObject currElem = (JSONObject) jsonElementArray.get(i);

				// ! This element has no type -> Inconsistent data from OSM. Please have a look.
				if (!currElem.containsKey("type")) {
					noTypeElements++;
					log.warn(Errors.TYPE_UNAVAILABLE.getMessage() + currElem.get("id").toString());
					continue;
				}

				// * -----------------Node Map Creation-------------------
				if (currElem.get("type").toString().equals("node")) {
					Long nodeId = Long.parseLong(currElem.get("id").toString());
					Integer version = Integer.parseInt(currElem.get("version").toString());
					Long changeSet = Long.parseLong(currElem.get("changeset").toString());
					String timestamp = currElem.get("timestamp").toString();
					String user = currElem.get("user").toString();
					Long userId = Long.parseLong(currElem.get("uid").toString());
					Double latitude = Double.parseDouble(currElem.get("lat").toString());
					Double longtitude = Double.parseDouble(currElem.get("lon").toString());

					if (nodeMap.containsKey(nodeId)) {
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
								Errors.DUPLICATE_NODE_ID.getMessage() + Long.toString(nodeId));
					}

					MapNode mapNode = new MapNode(nodeId, version, changeSet, timestamp, user, userId, latitude, longtitude);
					nodeMap.put(nodeId, mapNode);
				}

				// * -----------------Edge Map Creation-------------------
				if (currElem.get("type").toString().equals("way")) {
					JSONArray currWayNodeList = (JSONArray) currElem.get("nodes");
					double weight = 100;

					// ! Eliminate closed areas are they will cause errors for the algorithm
					// * When reading OSM data, you can identify closed ways by checking if the
					// * first and last nodes in the way are the same. If the first and last nodes
					// * are the same, then the way is closed and represents a closed polygon.
					long firstNodeInWay = Long.parseLong(currWayNodeList.get(0).toString());
					long lastNodeInWay = Long.parseLong(currWayNodeList.get(currWayNodeList.size() - 1).toString());

					if (firstNodeInWay == lastNodeInWay) {
						for (int closedAreaNodeIndex = 0; closedAreaNodeIndex < currWayNodeList.size(); closedAreaNodeIndex++) {
							long closedAreaNodeId = Long.parseLong(currWayNodeList.get(closedAreaNodeIndex).toString());
							closedNodeSet.add(closedAreaNodeId);
							if (nodeMap.containsKey(closedAreaNodeId)) {
								nodeMap.remove(closedAreaNodeId);
							}
						}
						continue;
					}

					// Check to see if we can update our weight for this edge
					if (!currElem.containsKey("tags")) {
						waysWithoutTag++;
						log.warn(Errors.TAG_UNAVAILABLE.getMessage() + currElem.get("id"));
					} else {
						JSONObject tagOject = (JSONObject) currElem.get("tags");
						try {
							weight = Double.parseDouble(tagOject.get("incline").toString());
						} catch (NumberFormatException | NullPointerException e) {
							waysWithInvalidIncline++;
							log.warn(Errors.INVALID_INCLINE_FORMAT.getMessage() + currElem.get("id"));
						}
					}

					for (int startIndex = 0; startIndex < currWayNodeList.size() - 1; startIndex++) {
						// -----------------Edge Map Data Processing-----------------
						long startNode = Long.parseLong(currWayNodeList.get(startIndex).toString());
						int endIndex = startIndex + 1;
						long endNode = Long.parseLong(currWayNodeList.get(endIndex).toString());

						/**
						 * * Eliminate some nodes that is not in the node map.
						 * * When downloading the data from OSM, it includes all paths that contains the
						 * * node within out predefined bound.
						 * * These paths usually contain some other nodes outside the bounds
						 * * -> We have to omit those nodes
						 */
						if (!nodeMap.containsKey(startNode) || !nodeMap.containsKey(endNode)) {
							continue;
						}

						List<Long> startNodeList = edgeMap.get(startNode);
						List<Long> endNodeList = edgeMap.get(endNode);

						if (startNodeList == null) {
							startNodeList = new ArrayList<Long>();
							startNodeList.add(endNode);
							edgeMap.put(startNode, startNodeList);
						} else {
							startNodeList.add(endNode);
						}

						if (endNodeList == null) {
							endNodeList = new ArrayList<Long>();
							endNodeList.add(startNode);
							edgeMap.put(endNode, endNodeList);
						} else {
							endNodeList.add(startNode);
						}

						// -----------------Weight Map Data Processing-----------------
						Pair<Long, Long> startEndNodePair = new Pair<Long, Long>(startNode, endNode);
						Pair<Long, Long> endStartNodePair = new Pair<Long, Long>(endNode, startNode);

						weightMap.put(startEndNodePair, weight);
						weightMap.put(endStartNodePair, weight);
					}
				}
			}

			// -----------------Clean up Weight and Edge Map-----------------
			/**
			 * * We need to clean this up becauase the main loop might missed some
			 * * nodes that is actually a part of a closed region
			 * * For example, take an node which is the entrance in Kreger Hall.
			 * * That node belongs to the closed region (the Kreger Hall Building)
			 * * But it is also belongs to an outer edge. We want to omit this
			 * * node since because it does not exist in the nodeMap (because it is deleted
			 * * when we encountered the closed region) but it does exist in the edgeMap
			 * * (because it shows up in the outer edge, which is not a closed region)
			 * * -> Causing problems to the algo.
			 */

			Iterator<Long> nodeIterator = edgeMap.keySet().iterator();
			while (nodeIterator.hasNext()) {
				Long nodeId = nodeIterator.next();
				List<Long> neighborList = edgeMap.get(nodeId);

				if (closedNodeSet.contains(nodeId)) {
					nodeIterator.remove(); // Remove the entire entry from the map
				}

				Iterator<Long> neighborIterator = neighborList.iterator();
				while (neighborIterator.hasNext()) {
					Long neighbor = neighborIterator.next();
					if (closedNodeSet.contains(neighbor)) {
						neighborIterator.remove(); // Remove the neighbor from the list
					}
				}
			}

			Iterator<Pair<Long, Long>> iterator = weightMap.keySet().iterator();
			while (iterator.hasNext()) {
				Pair<Long, Long> key = iterator.next();
				if (closedNodeSet.contains(key.getValue(0)) || closedNodeSet.contains(key.getValue(1))) {
					iterator.remove();
				}
			}

			log.info("Succesfully constructed the map");
			log.info(String.format("Node Map Size = %d", nodeMap.size()));
			log.info(String.format("Edge Map Size = %d", edgeMap.size()));
			log.info(String.format("Weight Map Size = %d", weightMap.size()));
			log.info(String.format("Number of elements without a type = %d", noTypeElements));
			log.info(String.format("Number of nodes within a closed region = %d", closedNodeSet.size()));
			log.info(String.format("Number of edges without a tag = %d", waysWithoutTag));
			log.info(String.format("Number of edges without a valid incline = %d", waysWithInvalidIncline));
		} catch (Exception e) {
			log.error(Errors.BUILD_ERROR.getMessage(), e);
			e.printStackTrace();
		}
	}

	public Map<Long, MapNode> getNodeMap() {
		return nodeMap;
	}

	public Map<Long, List<Long>> getEdgeMap() {
		return edgeMap;
	}

	public Map<Pair<Long, Long>, Double> getWeightMap() {
		return weightMap;
	}

	public MapNode getNode(long nodeId) {
		return nodeMap.get(nodeId);
	}
}
