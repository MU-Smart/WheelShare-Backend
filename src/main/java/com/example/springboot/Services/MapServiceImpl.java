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

import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Service
public class MapServiceImpl implements MapService {

	private static final Logger log = LoggerFactory.getLogger(MapServiceImpl.class);
	private Map<Integer, List<Integer>> nodeMap = new HashMap<Integer, List<Integer>>();
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
				
				List<Integer> itemsList = nodeMap.get(nodeId);

				if (itemsList == null)	{
					itemsList = new ArrayList<Integer>();
					itemsList.add(nodeId);
					nodeMap.put(nodeId, itemsList);
				}	else	{
					itemsList.add(nodeId);
				}

			}
		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
		}
	}

	public Map<Integer, List<Integer>> getNodeMap()	{
		return nodeMap;
	}
}
