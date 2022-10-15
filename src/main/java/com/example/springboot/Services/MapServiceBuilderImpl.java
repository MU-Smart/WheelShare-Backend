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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Service
public class MapServiceBuilderImpl implements MapServiceBuilder {

	private static final Logger log = LoggerFactory.getLogger(MapServiceBuilderImpl.class);
	private Map<Integer, MapNode> nodeMap = new HashMap<Integer, MapNode>();
	private Map<Integer, List<Integer>> edgeMap = new HashMap<Integer, List<Integer>>();

	JSONParser parser = new JSONParser();

	@Scheduled(fixedRate = 5000)
	public void buildMap() {
		try {
			String absoluteFilePath = new File("").getAbsolutePath();
			log.info(absoluteFilePath);

			Object jsonFileObject = parser.parse(new FileReader(absoluteFilePath + "/src/main/resources/sample-map-1.json"));
			JSONObject jsonObject = (JSONObject) jsonFileObject;

			nodeMap.clear();
			edgeMap.clear();

			/*
			 * Handles node list processing from file
			 */

			JSONArray jsonNodeList = (JSONArray) jsonObject.get("node");

			for (int i = 0; i < jsonNodeList.size(); i++) {
				JSONObject currNode = (JSONObject) jsonNodeList.get(i);

				Integer nodeId = Integer.parseInt(currNode.get("id").toString());
				Boolean visible = Boolean.parseBoolean(currNode.get("visible").toString());
				Integer version = Integer.parseInt(currNode.get("version").toString());
				Long changeSet = Long.parseLong(currNode.get("changeset").toString());
				String timestamp = currNode.get("timestamp").toString();
				String user = currNode.get("user").toString();
				Long userId = Long.parseLong(currNode.get("uid").toString());
				Float latitude = Float.parseFloat(currNode.get("lat").toString());
				Float longtitude = Float.parseFloat(currNode.get("lon").toString());

				if (nodeMap.containsKey(nodeId)) {
					throw new InvalidMetadataException(
							String.format("Duplicate node id Exception: Node %d. Please check the input files for errors.", nodeId));
				}
				MapNode mapNode = new MapNode(visible, version, changeSet, timestamp, user, userId, latitude, longtitude);
				nodeMap.put(nodeId, mapNode);
			}

			JSONArray wayList = (JSONArray) jsonObject.get("way");
			for (int i = 0; i < wayList.size(); i++) {
				JSONObject currWay = (JSONObject) wayList.get(i);
				JSONArray currWayNodeList = (JSONArray) currWay.get("nd");

				for (int startIndex = 0; startIndex < currWayNodeList.size(); startIndex++) {
					JSONObject startNodeObj = (JSONObject) currWayNodeList.get(startIndex);
					int startNode = Integer.parseInt(startNodeObj.get("ref").toString());

					for (int endIndex = startIndex + 1; endIndex < currWayNodeList.size(); endIndex++) {
						JSONObject endNodeObj = (JSONObject) currWayNodeList.get(endIndex);
						int endNode = Integer.parseInt(endNodeObj.get("ref").toString());
						
						List<Integer> startNodeList = edgeMap.get(startNode);
						List<Integer> endNodeList = edgeMap.get(endNode);

						if (startNodeList == null)	{
							startNodeList = new ArrayList<Integer>();
							startNodeList.add(endNode);
							edgeMap.put(startNode, startNodeList);
						}	else	{
							startNodeList.add(endNode);
						}

						if (endNodeList == null)	{
							endNodeList = new ArrayList<Integer>();
							endNodeList.add(startNode);
							edgeMap.put(endNode, endNodeList);
						}	else	{
							endNodeList.add(startNode);
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Error in buildMap function", e);
			e.printStackTrace();
		}
	}

	public Map<Integer, MapNode> getNodeMap() {
		return nodeMap;
	}

	public Map<Integer, List<Integer>> getEdgeMap() {
		return edgeMap;
	}
}
