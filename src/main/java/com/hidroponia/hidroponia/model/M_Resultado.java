package com.hidroponia.hidroponia.model;

import java.util.ArrayList;
import java.util.List;

public class M_Resultado {
    private boolean sucesso;
    private List<String> mensagem; // Para mensagens múltiplas
    private String alerta; // Para mensagens de alerta único

    // Construtor padrão
    public M_Resultado(boolean sucesso, String string, String mensagemString) {
        this.sucesso = sucesso;
        this.mensagem = new ArrayList<>(); // Inicializa a lista de mensagens
        this.alerta = ""; // Inicializa o alerta vazio
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
