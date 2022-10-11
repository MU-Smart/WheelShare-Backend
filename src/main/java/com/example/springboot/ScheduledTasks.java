package com.example.springboot;

import java.text.SimpleDateFormat;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	JSONParser parser = new JSONParser();

	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
		try {
			String filePath = new File("").getAbsolutePath();
			// creating a constructor of file class and parsing an XML file
			Object obj = parser.parse(new FileReader(filePath + "/src/main/resources/map.geojson"));
			JSONObject jsonObject =  (JSONObject) obj;
			JSONArray jsonArr = (JSONArray) jsonObject.get("features");

			for (int i=0; i < jsonArr.size(); i++) {
				JSONObject curr = (JSONObject) jsonArr.get(i);
				log.info(curr.get("properties").toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
