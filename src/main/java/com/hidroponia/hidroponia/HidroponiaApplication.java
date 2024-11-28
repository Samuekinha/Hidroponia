package com.hidroponia.hidroponia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
		scanBasePackages = "com.hidroponia",
		exclude = SecurityAutoConfiguration.class // Desativa a configuração de segurança padrão
)
@EnableScheduling
public class HidroponiaApplication {
	public static void main(String[] args) {
		SpringApplication.run(HidroponiaApplication.class, args);
	}
}
