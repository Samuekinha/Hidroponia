package com.hidroponia.hidroponia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class HidroponiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HidroponiaApplication.class, args);
	}

}
