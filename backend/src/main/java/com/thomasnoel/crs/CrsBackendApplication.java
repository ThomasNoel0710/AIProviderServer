package com.thomasnoel.crs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CrsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrsBackendApplication.class, args);
	}
}
