package com.hidroponia.hidroponia.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class M_irrigacaoStatus {
    private LocalDate irrigacaoAtualData = null; // ou inicialize com um novo objeto vazio
    private LocalTime irrigacaoAtualHora = null; // ou inicialize com um novo objeto vazio
    private Integer countdownSegundos = null;

    public LocalDate getIrrigacaoAtualData() {
        return irrigacaoAtualData;
    }

    public void setIrrigacaoAtualData(LocalDate irrigacaoAtualData) {
        this.irrigacaoAtualData = irrigacaoAtualData;
    }

    public LocalTime getIrrigacaoAtualHora() {
        return irrigacaoAtualHora;
    }

    public void setIrrigacaoAtualHora(LocalTime irrigacaoAtualHora) {
        this.irrigacaoAtualHora = irrigacaoAtualHora;
    }

    public Integer getCountdownSegundos() {
        return countdownSegundos;
    }

    public void setCountdownSegundos(Integer countdownSegundos) {
        this.countdownSegundos = countdownSegundos;
    }
}
