package com.example.springboot;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Service
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	public String result = "init value";

	JSONParser parser = new JSONParser();

	@Scheduled(fixedRate = 5000)
	public Set<String> reportCurrentTime() {
		Set<String> nodeSet = new HashSet<String>();
		try {
			String filePath = new File("").getAbsolutePath();
			log.info(filePath);
			// creating a constructor of file class and parsing an XML file
			Object obj = parser.parse(new FileReader(filePath + "/src/main/resources/sample-map-1.json"));
			JSONObject jsonObject =  (JSONObject) obj;

			/*
			 * Handles node list processing from file
			 */
			JSONArray nodeList = (JSONArray) jsonObject.get("node");

			for (int i=0; i < nodeList.size(); i++) {
				JSONObject currNode = (JSONObject) nodeList.get(i);
				String nodeId = currNode.get("id").toString();
				nodeSet.add(nodeId);
			}
			
			result = nodeSet.toString();

			System.out.println(nodeSet);
		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
		}

		return nodeSet;
	}
}
