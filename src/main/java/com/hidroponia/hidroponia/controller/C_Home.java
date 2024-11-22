package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_irrigacaoStatus;
import com.hidroponia.hidroponia.service.S_EnviaIrrigacao;
import jakarta.servlet.http.HttpSession;
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
    public String home(HttpSession session,
                       Model model) {
        M_irrigacaoStatus statusAtual = s_enviaIrrigacao.getStatusAtual();
        if (statusAtual == null) {
            statusAtual = new M_irrigacaoStatus();
        }
        model.addAttribute("statusAtual", statusAtual);
        return "/home"; // Nome do arquivo HTML
    }

}
