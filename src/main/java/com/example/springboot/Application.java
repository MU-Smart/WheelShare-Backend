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
		String absoluteFilePath = new File("").getAbsolutePath();
		FileInputStream serviceAccount = new FileInputStream(absoluteFilePath + "/firebase-key.json");
	
		FirebaseOptions options;
			options = new FirebaseOptions.Builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.build();
			FirebaseApp.initializeApp(options);

		SpringApplication.run(Application.class, args);
	}
}