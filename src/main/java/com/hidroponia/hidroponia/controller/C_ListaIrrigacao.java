package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.service.S_ListaIrrigacao;
import com.hidroponia.hidroponia.service.S_ListaUsuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class C_ListaIrrigacao {

    private final S_ListaIrrigacao s_listaIrrigacao;

    public C_ListaIrrigacao(S_ListaIrrigacao s_listaIrrigacao) {
        this.s_listaIrrigacao = s_listaIrrigacao;
    }

    @GetMapping("/lista-irrigacao")
    public String getListaIrrigacoes(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     Model model) {
        List<M_Irrigacao> irrigacao = s_listaIrrigacao.listarIrrigacoesPorPagina(page, size);
        model.addAttribute("irrigacao", irrigacao);
        return "lista-irrigacao"; // Retorna o nome da view "lista-irrigacao.html"
    }

}
