package com.example.springboot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@SpringBootApplication
@EnableScheduling
public class Application {
	public static void main(String[] args) throws IOException {
		// * Retrieve the credentials from file
		String absoluteFilePath = new File("").getAbsolutePath();
		System.out.println("Hello Thomas + " + absoluteFilePath);
		FileInputStream serviceAccount = new FileInputStream(absoluteFilePath + "/firebase-key.json");
		// * Load credentials into firebase and build it up
		FirebaseOptions options = FirebaseOptions.builder()
    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
    .build();
		// * Start the database
		FirebaseApp.initializeApp(options);
		// * Start the Spring Boot App
		SpringApplication.run(Application.class, args);
	}
}