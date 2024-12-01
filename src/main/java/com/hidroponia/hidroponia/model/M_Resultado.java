package com.hidroponia.hidroponia.model;

import java.util.ArrayList;
import java.util.List;

public class M_Resultado {
    private boolean sucesso;
    private List<String> mensagem; // Para mensagens múltiplas
    private String alerta; // Para mensagens de alerta único

    public M_Resultado() {
        this.mensagem = new ArrayList<>();
    }

    public M_Resultado(boolean podeAgendar, String alerta, String string) {
        this.sucesso = podeAgendar;
        this.alerta = alerta;
        this.mensagem = new ArrayList<>();
        this.mensagem.add(string); // Adiciona a mensagem passada ao construtor
    }

    // Getters e Setters
    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public List<String> getMensagem() {
        return mensagem;
    }

    public void addMensagem(String mensagem) {
        this.mensagem.add(mensagem); // Adiciona uma nova mensagem à lista
    }

    public void setMensagem(List<String> mensagens) {
        this.mensagem = mensagens; // Substitui a lista inteira de mensagens
    }

    public String getAlerta() {
        return alerta;
    }

    public void setAlerta(String alerta) {
        this.alerta = alerta; // Define o alerta
    }
}
