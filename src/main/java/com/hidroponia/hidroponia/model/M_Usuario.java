package com.hidroponia.hidroponia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class M_Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String senha;
    private String conf_senha;

    @Column(nullable = false, length = 20)
    private String nivel; // Novo campo para armazenar o nível de usuário

    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConf_senha() {
        return conf_senha;
    }

    public void setConf_senha(String conf_senha) {
        this.conf_senha = conf_senha;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}