package com.hidroponia.hidroponia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
=======

@SpringBootApplication
>>>>>>> ebba9967459b64532e646185f976f83d3093f2a3
public class HidroponiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HidroponiaApplication.class, args);
	}

}
