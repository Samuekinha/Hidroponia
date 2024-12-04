package com.hidroponia.hidroponia.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "irrigacao_pattern")
public class M_IrrigacaoPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duracao")
    private Integer duracao;

    @Column(name = "intervalo")
    private Integer intervalo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", foreignKey = @ForeignKey(name = "FK_comentario"))
    private M_Usuario usuario; // Alterado para ser um objeto e não um ID

    @ManyToOne
    @JoinColumn(name = "id_comentario", foreignKey = @ForeignKey(name = "FK_comentario"))
    private M_Comentario comentario; // Alterado para ser um objeto e não um ID

    @Column(name = "active")
    private Boolean active;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public Integer getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Integer intervalo) {
        this.intervalo = intervalo;
    }

    public M_Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(M_Usuario usuario) {
        this.usuario = usuario;
    }

    public M_Comentario getComentario() {
        return comentario;
    }

    public void setComentario(M_Comentario comentario) {
        this.comentario = comentario;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
