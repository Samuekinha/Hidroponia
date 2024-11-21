package com.hidroponia.hidroponia.model;

public class M_irrigacaoStatus {

    private M_Irrigacao irrigacaoAtual;
    private Integer countdownSegundos;

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
