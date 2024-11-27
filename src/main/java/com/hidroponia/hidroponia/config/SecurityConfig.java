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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF (caso não esteja usando)
                .authorizeRequests()
                // Permite o acesso público para a página de login, logout e recursos estáticos (CSS, JS, etc)
                .requestMatchers("/", "/login", "/logout", "/css/**", "/js/**").permitAll()
                // Exige autenticação para outras URLs
                .anyRequest().authenticated()
                .and()
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")  // Define a página de login personalizada
                        .loginProcessingUrl("/login") // Define a URL para processar o login
                        .defaultSuccessUrl("/home", true)  // Redireciona para /home após o login bem-sucedido
                        .failureUrl("/login?error=true") // Redireciona para o login com erro
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para logout
                        .invalidateHttpSession(true)  // Invalida a sessão
                        .clearAuthentication(true)   // Limpa a autenticação
                        .logoutSuccessUrl("/") // Redireciona para a página inicial após logout
                );

        return http.build();
    }

}
