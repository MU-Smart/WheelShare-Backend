package com.example.springboot.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.metadata.InvalidMetadataException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.springboot.Models.MapNode;

import java.io.FileReader;

import org.javatuples.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Service
public class MapServiceBuilderImpl implements MapServiceBuilder {

	private static final Logger log = LoggerFactory.getLogger(MapServiceBuilderImpl.class);
	private Map<Long, MapNode> nodeMap = new HashMap<Long, MapNode>();
	private Map<Long, List<Long>> edgeMap = new HashMap<Long, List<Long>>();
	private Map<Pair<Long, Long>, Double> weightMap = new HashMap<Pair<Long, Long>, Double>();

	JSONParser parser = new JSONParser();

	@Scheduled(fixedRate = 60000)
	public void buildMap() {
		try {
			String absoluteFilePath = new File("").getAbsolutePath();
			log.info(absoluteFilePath);

			// * Read the file in
			Object jsonFileObject = parser.parse(new FileReader(absoluteFilePath + "/src/main/resources/mapData.json"));
			// Object jsonFileObject = parser.parse(new FileReader(absoluteFilePath +
			// "/routing/mapData.json"));
			JSONObject jsonObject = (JSONObject) jsonFileObject;

			// * Clean up all of the hashmaps to put new data in
			// ?: Is there any way we can pull new data in without having to clear the
			// hashmaps?
			nodeMap.clear();
			edgeMap.clear();
			weightMap.clear();

			JSONObject osmJsonObject = (JSONObject) jsonObject.get("osm");
			JSONArray jsonNodeList = (JSONArray) osmJsonObject.get("node");

			// -----------------Node Map-----------------
			for (int i = 0; i < jsonNodeList.size(); i++) {
				JSONObject currNode = (JSONObject) jsonNodeList.get(i);

				Long nodeId = Long.parseLong(currNode.get("@id").toString());
				Integer version = Integer.parseInt(currNode.get("@version").toString());
				Long changeSet = Long.parseLong(currNode.get("@changeset").toString());
				String timestamp = currNode.get("@timestamp").toString();
				String user = currNode.get("@user").toString();
				Long userId = Long.parseLong(currNode.get("@uid").toString());
				Double latitude = Double.parseDouble(currNode.get("@lat").toString());
				Double longtitude = Double.parseDouble(currNode.get("@lon").toString());

				if (nodeMap.containsKey(nodeId)) {
					throw new InvalidMetadataException(
							String.format("Duplicate node id Exception: Node %d. Please check the input files for errors.", nodeId));
				}
				MapNode mapNode = new MapNode(version, changeSet, timestamp, user, userId, latitude, longtitude);
				nodeMap.put(nodeId, mapNode);
			}

			// -----------------Edge and Weight Map-----------------
			JSONArray wayList = (JSONArray) osmJsonObject.get("way");
			int unlabelledWayCount = 0; // a counter to count how many ways does not have a valid incline

			for (int i = 0; i < wayList.size(); i++) {
				JSONObject currWay = (JSONObject) wayList.get(i);
				JSONArray currWayNodeList = (JSONArray) currWay.get("nd");
				double weight = 1;

				/**
				 * * Check for the incline inside each way.
				 * * If there is, set the weight var to that value
				 * * If not, log out an error and increment the var unlabelledWayCount
				 * ! Sometimes, the incline value existed but it is not parsable
				 */
				if (currWay.get("tag") == null) {
					unlabelledWayCount++;
					log.warn(String.format("Tag not available for this way: Id - %s", currWay.get("@id")));
				} else {

					String tagType = currWay.get("tag").getClass().toString();

					// * Tag is a JSON Array
					if (tagType.equals("class org.json.simple.JSONArray")) {
						JSONArray tagList = (JSONArray) currWay.get("tag");
						for (int index = 0; index < tagList.size(); index++) {
							JSONObject currTag = (JSONObject) tagList.get(index);

							if (currTag.get("@k").toString().equals("incline")) {
								try {
									weight = Double.parseDouble(currTag.get("@v").toString());
								} catch (NumberFormatException e) {
									unlabelledWayCount++;
									log.info(String.format("Incline is not a number at way : %s", currWay.get("@id")));
								}
							}
						}
					}

					// * Tag is a JSON Array
					if (tagType.equals("class org.json.simple.JSONObject")) {
						JSONObject tagOject = (JSONObject) currWay.get("tag");
						if (tagOject.get("@k").toString().equals("incline")) {
							try {
								weight = Double.parseDouble(tagOject.get("@v").toString());
							} catch (NumberFormatException e) {
								unlabelledWayCount++;
								log.info(String.format("Incline is not a number at way : %s", currWay.get("@id")));
							}
						}
					}
				}

				for (int startIndex = 0; startIndex < currWayNodeList.size(); startIndex++) {
					// -----------------Edge Map Data Processing-----------------
					JSONObject startNodeObj = (JSONObject) currWayNodeList.get(startIndex);
					long startNode = Long.parseLong(startNodeObj.get("@ref").toString());

					for (int endIndex = startIndex + 1; endIndex < currWayNodeList.size(); endIndex++) {
						JSONObject endNodeObj = (JSONObject) currWayNodeList.get(endIndex);
						long endNode = Long.parseLong(endNodeObj.get("@ref").toString());

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

			log.info("Succesfully constructed the map");
			log.info(String.format("Node Map Size = %d", nodeMap.size()));
			log.info(String.format("Edge Map Size = %d", edgeMap.size()));
			log.info(String.format("Weight Map Size = %d", weightMap.size()));
			log.info(String.format("Number of ways that missed valid incline value = %d", unlabelledWayCount));
		} catch (Exception e) {
			log.error("Error in buildMap function", e);
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
}
