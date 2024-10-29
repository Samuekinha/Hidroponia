package com.hidroponia.hidroponia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/login", "/home").permitAll()  // Permitir acesso às páginas de login e home
                        .anyRequest().authenticated()  // Todas as outras requisições precisam estar autenticadas
                )
                .formLogin((form) -> form
                        .loginPage("/login")  // Definir a página de login customizada
                        .defaultSuccessUrl("/home")  // Redirecionar para home após login bem-sucedido
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")  // URL para logout
                        .logoutSuccessUrl("/login?logout=true")  // Redirecionar após logout
                        .permitAll());  // Permitir logout para todos

        return http.build();
    }

    // Configuração de usuário em memória (para testar autenticação)
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")  // Nome de usuário para teste
                .password("password")  // Senha para teste
                .roles("USER")  // Papel do usuário
                .build();

        return new InMemoryUserDetailsManager(user);  // Retorna um gerenciador de usuários em memória
    }

}