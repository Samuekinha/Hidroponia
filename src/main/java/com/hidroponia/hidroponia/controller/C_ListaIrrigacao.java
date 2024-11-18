package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.service.S_ListaIrrigacao;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class C_ListaIrrigacao {

    private final S_ListaIrrigacao s_listaIrrigacao;

    public C_ListaIrrigacao(S_ListaIrrigacao s_listaIrrigacao) {
        this.s_listaIrrigacao = s_listaIrrigacao;
    }

    @GetMapping("/lista-irrigacao")
    public String getListaIrrigacoes() {
        List<M_Irrigacao> irrigacao = s_listaIrrigacao.listarIrrigacoesPorPagina();

        return "lista-irrigacao"; // Retorna o nome da view "lista-irrigacao.html"
    }


}
