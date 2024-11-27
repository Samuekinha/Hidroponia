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

        M_irrigacaoStatus statusAtual = s_enviaIrrigacao.getStatusAtual();
        if (statusAtual == null) {
            statusAtual = new M_irrigacaoStatus();
        }

        model.addAttribute("statusAtual", statusAtual);

        if (username != null) {
            model.addAttribute("message", "Bem-vindo, " + username + "!");
            return "/home"; // Nome do arquivo HTML
        } else {
            model.addAttribute("message", "Bem-vindo! Faça login.");
            return "/";
        }
    }

    @GetMapping("/hometwo")
    public ResponseEntity<Map<String, Object>> getHomeData(HttpSession session) {
        String username = (String) session.getAttribute("username");

        // Verifica se o usuário está autenticado
        Map<String, Object> response = new HashMap<>();
        if (username != null) {
            response.put("message", "Bem-vindo, " + username + "!");
        } else {
            response.put("message", "Bem-vindo! Faça login.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);  // Retorna 401 Unauthorized
        }

        // Tenta recuperar o status de irrigação
        try {
            M_irrigacaoStatus statusAtual = s_enviaIrrigacao.getStatusAtual();
            if (statusAtual == null) {
                statusAtual = new M_irrigacaoStatus();  // Garantir que nunca seja null
            }

            Map<String, Object> data = new HashMap<>();
            data.put("irrigacaoAtualData", statusAtual.getIrrigacaoAtualData());
            data.put("irrigacaoAtualHora", statusAtual.getIrrigacaoAtualHora());
            data.put("countdownSegundos", statusAtual.getCountdownSegundos());

            response.put("irrigacaoStatus", data);  // Adiciona os dados de irrigação na resposta

            return ResponseEntity.ok(response);  // Retorna os dados com status 200 OK

        } catch (Exception e) {
            e.printStackTrace();  // Exibe o erro no log
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Retorna 500 em caso de erro
        }
    }

}