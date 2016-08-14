package com.guxl.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class UploadApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploadApplication.class, args);
		
		System.out.println("===================================");
		System.out.println(System.getProperty("user.dir"));
	}
}
