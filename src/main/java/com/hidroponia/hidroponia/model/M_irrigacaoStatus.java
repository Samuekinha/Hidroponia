package com.hidroponia.hidroponia.model;

import com.hidroponia.hidroponia.model.M_Irrigacao;

public class M_irrigacaoStatus {
    private M_Irrigacao irrigacaoAtual = null; // ou inicialize com um novo objeto vazio
    private Integer countdownSegundos = null;

    // Getters e Setters
    public M_Irrigacao getIrrigacaoAtual() {
        return irrigacaoAtual;
    }

    public void setIrrigacaoAtual(M_Irrigacao irrigacaoAtual) {
        this.irrigacaoAtual = irrigacaoAtual;
    }

    public Integer getCountdownSegundos() {
        return countdownSegundos;
    }

    public void setCountdownSegundos(Integer countdownSegundos) {
        this.countdownSegundos = countdownSegundos;
    }
}
