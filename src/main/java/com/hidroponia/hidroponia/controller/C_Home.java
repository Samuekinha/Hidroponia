package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.model.M_irrigacaoStatus;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import com.hidroponia.hidroponia.service.S_EnviaIrrigacao;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class C_Home {

    private final S_EnviaIrrigacao s_enviaIrrigacao;
    private final R_Irrigacao r_irrigacao;

    public C_Home(S_EnviaIrrigacao s_enviaIrrigacao, R_Irrigacao r_irrigacao) {
        this.s_enviaIrrigacao = s_enviaIrrigacao;
        this.r_irrigacao = r_irrigacao;
    }

    @GetMapping("/home")
    public String home(HttpSession session,
                       Model model) {

        String username = (String) session.getAttribute("username");

        if (username != null) {

            M_irrigacaoStatus statusAtual = s_enviaIrrigacao.getStatusAtual();
            Optional<M_Irrigacao> irrigacaoHome = r_irrigacao.findLastIrrigacaoBefore(LocalDate.now(), LocalTime.now());

            if (statusAtual == null) {
                statusAtual = new M_irrigacaoStatus();
            }

            model.addAttribute("statusAtual", statusAtual);

            irrigacaoHome.ifPresent(ultimaIrrigacao -> {
                model.addAttribute("infoSensores", ultimaIrrigacao);
                model.addAttribute("temperatura", ultimaIrrigacao.getTemperatura());
                model.addAttribute("umidade", ultimaIrrigacao.getUmidade());
                model.addAttribute("pontoOrvalho", ultimaIrrigacao.getPontoOrvalho());
            });


            model.addAttribute("userNomes", username);

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