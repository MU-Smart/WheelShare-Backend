package com.example.springboot.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
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
	private Map<Integer, List<MapNode>> nodeMap = new HashMap<Integer, List<MapNode>>();


	JSONParser parser = new JSONParser();
	
	@Scheduled(fixedRate = 5000)
	public void buildMap() {
		try {
			String filePath = new File("").getAbsolutePath();
			log.info(filePath);
			// creating a constructor of file class and parsing an XML file
			Object obj = parser.parse(new FileReader(filePath + "/src/main/resources/sample-map-1.json"));
			JSONObject jsonObject =  (JSONObject) obj;
			nodeMap.clear();
			/*
			 * Handles node list processing from file
			 */
			JSONArray nodeList = (JSONArray) jsonObject.get("node");

			for (int i=0; i < nodeList.size(); i++) {
				JSONObject currNode = (JSONObject) nodeList.get(i);

				Integer nodeId = Integer.parseInt(currNode.get("id").toString());
				Boolean visible = Boolean.parseBoolean(currNode.get("visible").toString());
				Integer version = Integer.parseInt(currNode.get("version").toString());
				Long changeSet = Long.parseLong(currNode.get("changeset").toString());
				String timestamp = currNode.get("timestamp").toString();
				String user = currNode.get("user").toString();
				Long userId = Long.parseLong(currNode.get("uid").toString());
				Float latitude = Float.parseFloat(currNode.get("lat").toString());
				Float longtitude = Float.parseFloat(currNode.get("lon").toString());
				
				List<MapNode> itemsList = nodeMap.get(nodeId);
				MapNode mapNode = new MapNode(visible, version, changeSet, timestamp, user, userId, latitude, longtitude);

				if (itemsList == null)	{
					itemsList = new ArrayList<MapNode>();
					itemsList.add(mapNode);
					nodeMap.put(nodeId, itemsList);
				}	else	{
					itemsList.add(mapNode);
				}
			}
		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
		}
	}

	public Map<Integer, List<MapNode>> getNodeMap()	{
		return nodeMap;
	}
}
