package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.service.S_EnviaIrrigacao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class C_Home {

    private final S_EnviaIrrigacao s_enviaIrrigacao;

    public C_Home(S_EnviaIrrigacao s_enviaIrrigacao) {
        this.s_enviaIrrigacao = s_enviaIrrigacao;
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("statusAtual", s_enviaIrrigacao.getStatusAtual());
        return "/home"; // Nome do arquivo HTML
    }

    @GetMapping("/homeIrriga")
    @ResponseBody
    public Object statusAtual() {
        return s_enviaIrrigacao.getStatusAtual();
    }
}