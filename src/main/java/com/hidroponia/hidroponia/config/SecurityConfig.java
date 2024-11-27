package com.hidroponia.hidroponia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF (apenas para testes; habilite em produção)
                .authorizeRequests(auth -> auth
                        .requestMatchers("/", "/login", "/logout").permitAll() // Permite acesso a essas URLs
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // Libera arquivos estáticos CSS, JS e imagens
                        .anyRequest().authenticated() // Todas as outras URLs requerem autenticação
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Define a URL para logout
                        .invalidateHttpSession(true) // Invalida a sessão
                        .clearAuthentication(true) // Limpa autenticação
                        .logoutSuccessUrl("/") // Redireciona para a página inicial após logout
                );
        return http.build();
    }
}
