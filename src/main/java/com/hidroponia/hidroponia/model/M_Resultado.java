package com.hidroponia.hidroponia.model;

import java.util.ArrayList;
import java.util.List;

public class M_Resultado {
    private boolean sucesso;
    private String alerta; // Para mensagens de alerta único

    public M_Resultado(boolean podeAgendar, String alerta) {
        this.sucesso = podeAgendar;
        this.alerta = alerta;
    }

    public M_Resultado() {

    }

    // Getters e Setters
    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getAlerta() {
        return alerta;
    }

    public void setAlerta(String alerta) {
        this.alerta = alerta; // Define o alerta
    }
}
