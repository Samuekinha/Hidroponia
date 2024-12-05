package com.hidroponia.hidroponia.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "irrigacao")
public class M_Irrigacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperatura;

    @Column(precision = 5, scale = 2)
    private BigDecimal umidade;  // Adicionado umidade

    @Column(precision = 5, scale = 2)
    private BigDecimal pontoOrvalho; // Adicionado pontoOrvalho

    @Column(name = "data_registro", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dataRegistro;

    @Column(name = "data_irrigacao", nullable = false) // garante que seja not null
    private LocalDate dataIrrigacao;

    @Column(name = "hora_irrigacao", nullable = false) // garante que seja not null
    private LocalTime horaIrrigacao;

    @Column(name = "intervalo")
    private Integer intervalo;

    @Column(name = "concluida", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean concluida;

    @ManyToOne
    @JoinColumn(name = "comentario_id", foreignKey = @ForeignKey(name = "FK_comentario"))
    private M_Comentario comentario; // Alterado para ser um objeto e não um ID;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(BigDecimal temperatura) {
        this.temperatura = temperatura;
    }

    public BigDecimal getUmidade() {
        return umidade;
    }

    public void setUmidade(BigDecimal umidade) {
        this.umidade = umidade;
    }

    public BigDecimal getPontoOrvalho() {
        return pontoOrvalho;
    }

    public void setPontoOrvalho(BigDecimal pontoOrvalho) {
        this.pontoOrvalho = pontoOrvalho;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public LocalDate getDataIrrigacao() {
        return dataIrrigacao;
    }

    public void setDataIrrigacao(LocalDate dataIrrigacao) {
        this.dataIrrigacao = dataIrrigacao;
    }

    public LocalTime getHoraIrrigacao() {
        return horaIrrigacao;
    }

    public void setHoraIrrigacao(LocalTime horaIrrigacao) {
        this.horaIrrigacao = horaIrrigacao;
    }

    public Integer getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Integer intervalo) {
        this.intervalo = intervalo;
    }

    public Boolean getConcluida() {
        return concluida;
    }

    public void setConcluida(Boolean concluida) {
        this.concluida = concluida;
    }

    public M_Comentario getComentario() {
        return comentario;
    }

    public void setComentario(M_Comentario comentario) {
        this.comentario = comentario;
    }

}
