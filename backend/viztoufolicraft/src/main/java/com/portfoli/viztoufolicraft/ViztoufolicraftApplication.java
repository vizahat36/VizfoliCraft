package com.portfoli.viztoufolicraft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.portfoli.viztoufolicraft", "com.yourcompany.portfoliogenerator"})
@EnableScheduling
public class ViztoufolicraftApplication {

	public static void main(String[] args) {
		SpringApplication.run(ViztoufolicraftApplication.class, args);
	}

}
