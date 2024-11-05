package com.hidroponia.hidroponia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "comentario")
public class M_Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String texto; // Campo para o texto do comentário

    @ManyToOne
    @JoinColumn(name = "usuario_id", foreignKey = @ForeignKey(name = "FK_USUARIO_COMENTARIO"))
    private M_Usuario usuario; // Relacionamento com a entidade Usuario

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public M_Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(M_Usuario usuario) {
        this.usuario = usuario;
    }

    // Outros métodos, se necessário
}
