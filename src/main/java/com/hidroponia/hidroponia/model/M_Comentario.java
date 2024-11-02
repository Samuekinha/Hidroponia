package com.hidroponia.hidroponia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "comentario")
public class M_Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)//garante que seja not null
    private String corpo; // O campo de Corpo do comentário

    // Outros campos que você desejar

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    // Outros métodos
}
