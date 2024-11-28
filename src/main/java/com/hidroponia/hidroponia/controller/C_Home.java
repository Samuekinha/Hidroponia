package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_irrigacaoStatus;
import com.hidroponia.hidroponia.service.S_EnviaIrrigacao;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class C_Home {

    private final S_EnviaIrrigacao s_enviaIrrigacao;

    public C_Home(S_EnviaIrrigacao s_enviaIrrigacao) {
        this.s_enviaIrrigacao = s_enviaIrrigacao;
    }

    @GetMapping("/home")
    public String home(HttpSession session,
                        Model model) {

        String username = (String) session.getAttribute("username");

        if (username != null) {
            M_irrigacaoStatus statusAtual = s_enviaIrrigacao.getStatusAtual();
            if (statusAtual == null) {
                statusAtual = new M_irrigacaoStatus();
            }

            model.addAttribute("statusAtual", statusAtual);

            model.addAttribute("message", "Bem-vindo, " + username + "!");
            return "/home"; // Nome do arquivo HTML
        } else {
            model.addAttribute("message", "Bem-vindo! Faça login.");
            return "redirect:/";
        }
    }

    @GetMapping("/hometwo")
    public ResponseEntity<Map<String, Object>> getHomeData() {
        try {
            M_irrigacaoStatus statusAtual = s_enviaIrrigacao.getStatusAtual();
            if (statusAtual == null) {
                statusAtual = new M_irrigacaoStatus();  // Garantir que nunca seja null
            }

            Map<String, Object> data = new HashMap<>();
            data.put("irrigacaoAtualData", statusAtual.getIrrigacaoAtualData());  // Ex: "2024-11-23"
            data.put("irrigacaoAtualHora", statusAtual.getIrrigacaoAtualHora());  // Ex: "12:30"
            data.put("countdownSegundos", statusAtual.getCountdownSegundos());  // Ex: "60"

            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace();  // Exibe o erro no log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}