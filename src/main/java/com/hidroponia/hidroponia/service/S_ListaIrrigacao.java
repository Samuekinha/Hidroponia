package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class S_ListaIrrigacao {

    private final R_Irrigacao r_irrigacao;

    public S_ListaIrrigacao(R_Irrigacao r_irrigacao) {
        this.r_irrigacao = r_irrigacao;
    }

    // Método para buscar irrigação por página
    public List<M_Irrigacao> listarIrrigacoes() {
        return r_irrigacao.findAll();
    }


}
