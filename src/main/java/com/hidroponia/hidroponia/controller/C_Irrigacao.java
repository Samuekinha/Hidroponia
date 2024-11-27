package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.model.M_irrigacaoStatus;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import com.hidroponia.hidroponia.service.S_AgendaIrrigacao;

import com.hidroponia.hidroponia.service.S_ListaIrrigacao;
import jakarta.servlet.http.HttpSession;
import org.aspectj.bridge.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/irrigacao")
public class C_Irrigacao {

    private final S_AgendaIrrigacao s_agendaIrrigacao;
    private final R_Irrigacao r_irrigacao;
    private final S_ListaIrrigacao s_listaIrrigacao;

    public C_Irrigacao(S_AgendaIrrigacao sAgendaIrrigacao, R_Irrigacao rIrrigacao, S_ListaIrrigacao sListaIrrigacao) {
        s_agendaIrrigacao = sAgendaIrrigacao;
        r_irrigacao = rIrrigacao;
        s_listaIrrigacao = sListaIrrigacao;
    }

    @GetMapping("/agendar")
    public String mostrarProximasIrrigacoes(Model model, HttpSession session) {

        String username = (String) session.getAttribute("username");
        String nivel = (String) session.getAttribute("nivel");

        // Verifica se o usuário está autenticado
        if (username == null) {
            model.addAttribute("message", "Faça login para acessar este recurso.");
            return "redirect:/login"; // Redireciona para a página de login
        }

        // Verifica se o usuário tem permissão para realizar a ação
        if ("USER".equals(nivel)) {
            // Retorna uma mensagem de erro para ser mostrada no frontend
            model.addAttribute("message", "Você não tem permissão para realizar esta ação.");
            return "/error-frag"; // Retorna para a página com a modal de erro
        }

        // Caso o usuário tenha permissão, busca as próximas irrigacões
        List<M_Irrigacao> proximasIrrigacoes = s_agendaIrrigacao.obterProximasIrrigacoes();
        model.addAttribute("proximasIrrigacoes", proximasIrrigacoes);

        return "/agendar-irrigacao"; // Retorna a página de agendamento
    }


    @GetMapping("/agendarLista")
    @ResponseBody
    public ResponseEntity<?> listarProximasIrrigacoes(HttpSession session){
        String username = (String) session.getAttribute("username");

        if (username == null) {
            // Retorna resposta de erro com status 401 se o usuário não estiver autenticado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login para acessar este recurso.");
        }

        // Caso o usuário esteja autenticado, retorna a lista de irrigação com status 200
        List<M_Irrigacao> irrigacoes = s_agendaIrrigacao.obterProximasIrrigacoes();
        return ResponseEntity.ok(irrigacoes);
    }


    @PostMapping("/agendar")
    @ResponseBody
    public ResponseEntity<String> postAgendarIrrig(@RequestParam("datairrigacao") LocalDate dataIrrigacao,
                                                   @RequestParam("horairrigacao") LocalTime horaIrrigacao,
                                                   @RequestParam("intervalo") Integer intervalo,
                                                   HttpSession session) {

        String username = (String) session.getAttribute("username");
        String nivel = (String) session.getAttribute("nivel");

        // Verifica se o usuário está autenticado
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login para acessar este recurso.");
        }

        // Verifica se o usuário tem permissão para realizar a ação
        if ("USER".equals(nivel)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para realizar esta ação.");
        }

        // Valida e agenda a irrigação
        if (s_agendaIrrigacao.validaAgendaIrrigacao(dataIrrigacao, horaIrrigacao, intervalo)) {
            return ResponseEntity.ok("Irrigação agendada com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao agendar a irrigação.");
        }
    }

    @GetMapping("/listar")
    public String getListaIrrigacoes(Model model,
                                     HttpSession session) {

        String username = (String) session.getAttribute("username");

        if (username == null) {
            model.addAttribute("message", "Faça login antes!");
            return "/"; // Nome do arquivo HTML
        }

        List<M_Irrigacao> irrigacao = s_listaIrrigacao.listarIrrigacoes();
        model.addAttribute("irrigacao", irrigacao);

        return "/lista-irrigacao"; // Retorna o nome da view "lista-irrigacao.html"
    }

    @PostMapping("/atualizar")
    @ResponseBody
    public ResponseEntity<String> atualizarUsuario(@RequestParam("id") Long id,
                                                     @RequestParam("datairrigacao") LocalDate dataIrrigacao,
                                                     @RequestParam("horairrigacao") LocalTime horaIrrigacao,
                                                     @RequestParam("intervalo") Integer intervalo,
                                                     HttpSession session) {

        String username = (String) session.getAttribute("username");
        String nivel = (String) session.getAttribute("nivel");

        // Verifica se o usuário está autenticado
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Faça login para acessar este recurso.");
        }

        // Verifica se o usuário tem permissão para realizar a ação
        if ("USER".equals(nivel)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para realizar esta ação.");
        }

        try {
            // Chamada ao serviço
            boolean atualizado = s_agendaIrrigacao.atualizarAtividade(id, dataIrrigacao, horaIrrigacao, intervalo);

            if (atualizado) {
                // Retorna uma resposta de sucesso
                return ResponseEntity.ok("Irrigação atualizada com sucesso!");
            } else {
                // Retorna status 404 caso o ID não seja encontrado ou não atualizado
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: irrigação não encontrada.");
            }
        } catch (Exception e) {
            // Retorna status 500 em caso de erro interno
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar a irrigação: " + e.getMessage());
        }
    }


    @PostMapping("/deletar")
    @ResponseBody
    public String deletarAtividade(@RequestParam("id") Long id,
                                   Model model,
                                   HttpSession session) {

        String username = (String) session.getAttribute("username");
        String nivel = (String) session.getAttribute("nivel");

        // Verifica se o usuário está autenticado
        if (username == null) {
            model.addAttribute("message","Faça login para acessar este recurso.");
        }

        // Verifica se o usuário tem permissão para realizar a ação
        if ("USER".equals(nivel)) {
            model.addAttribute("message", "Você não tem permissão para realizar esta ação.");
        }

        S_AgendaIrrigacao.deletarAtividade(id);
        model.addAttribute("message", "Irrigação deletada com sucesso!");
        return "/fragment/empty";
    }

    @GetMapping("/{id}")
    public ResponseEntity<M_Irrigacao> getIrrigacaoById(@PathVariable Long id) {
        M_Irrigacao irrigacao = r_irrigacao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Irrigação não encontrada."));
        return ResponseEntity.ok(irrigacao);
    }

}

