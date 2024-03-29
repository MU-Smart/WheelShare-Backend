package com.wheelshare.springboot.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	private HashSet<Long> openNodeSet = new HashSet<Long>();

	JSONParser parser = new JSONParser();

	// This function is executed every 5 minutes
	@Scheduled(fixedRate = 300000)
	public void buildMap() {
		try {
			String absoluteFilePath = new File("").getAbsolutePath();

			// * Read the file in
			// Local mapData location
			// Object jsonFileObject = parser.parse(new FileReader(absoluteFilePath +
			// 		"/src/main/resources/mapData.json"));
					
			// Server mapData location
			Object jsonFileObject = parser.parse(new FileReader(absoluteFilePath +
			"/mapData.json"));
			
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

					long firstNodeInWay = Long.parseLong(currWayNodeList.get(0).toString());
					long lastNodeInWay = Long.parseLong(currWayNodeList.get(currWayNodeList.size() - 1).toString());

					/**
					 * * We are adding nodes into 2 seperate set: closedNodeSet and openNodeSet
					 * * closedNodeSet is the set containing nodes belonging to a closed region/way
					 * * openNodeSet is the set containing nodes belonging to an open region/way
					 */
					if (firstNodeInWay == lastNodeInWay) {
						for (int nodeIndex = 0; nodeIndex < currWayNodeList.size(); nodeIndex++) {
							long nodeIndexId = Long.parseLong(currWayNodeList.get(nodeIndex).toString());
							closedNodeSet.add(nodeIndexId);
						}
					} else {
						for (int nodeIndex = 0; nodeIndex < currWayNodeList.size(); nodeIndex++) {
							long nodeIndexId = Long.parseLong(currWayNodeList.get(nodeIndex).toString());
							openNodeSet.add(nodeIndexId);
						}
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

			// -----------------Data Clean Up-----------------
			/**
			 * * This part is to clean up all the nodes that
			 * * only belongs to the closedNodeSet
			 * * These node lies within a closed region so they are not useful
			 * * for what we are considering.
			 */
			for (long nodeId : closedNodeSet) {
				if (!openNodeSet.contains(nodeId)) {
					// remove this node from the node map
					nodeMap.remove(nodeId);

					// check for every of its neighbor
					for (long neighborId : edgeMap.get(nodeId)) {
						List<Long> neighborNeighBorList = edgeMap.get(neighborId);
						// remove it from the neighbor's neighbor list in the edge map
						neighborNeighBorList.remove(nodeId);

						// remove the pair of this node and the current neighbor in the weight map
						Pair<Long, Long> startEndNodePair = new Pair<Long, Long>(nodeId, neighborId);
						Pair<Long, Long> endStartNodePair = new Pair<Long, Long>(neighborId, nodeId);
						weightMap.remove(startEndNodePair);
						weightMap.remove(endStartNodePair);
					}

					// remove this node from the edge map
					// At this point, this node basically does not exist in our data
					edgeMap.remove(nodeId);
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
