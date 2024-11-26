package com.hidroponia.hidroponia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableAsync  // Habilita o suporte a tarefas assíncronas no Spring Boot
    public class AsyncConfig {
}

