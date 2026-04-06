package com.healthbridge.healthbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HealthbridgeApplication {
	public static void main(String[] args) {
		SpringApplication.run(HealthbridgeApplication.class, args);
	}
}