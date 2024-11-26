package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import com.hidroponia.hidroponia.service.S_AgendaIrrigacao;

import com.hidroponia.hidroponia.service.S_ListaIrrigacao;
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
public class C_CadastrarIrrigacao {

    private final S_AgendaIrrigacao s_agendaIrrigacao;
    private final R_Irrigacao r_irrigacao;
    private final S_ListaIrrigacao s_listaIrrigacao;

    public C_CadastrarIrrigacao(S_AgendaIrrigacao sAgendaIrrigacao, R_Irrigacao rIrrigacao, S_ListaIrrigacao sListaIrrigacao) {
        s_agendaIrrigacao = sAgendaIrrigacao;
        r_irrigacao = rIrrigacao;
        s_listaIrrigacao = sListaIrrigacao;
    }

    @GetMapping("/agendar")
    public String mostrarProximasIrrigacoes(Model model) {
        List<M_Irrigacao> proximasIrrigacoes = s_agendaIrrigacao.obterProximasIrrigacoes();
        model.addAttribute("proximasIrrigacoes", proximasIrrigacoes);
        return "/agendar-irrigacao"; // Retorna um fragmento específico
    }

    @GetMapping("/agendarLista")
    @ResponseBody
    public List<M_Irrigacao> listarProximasIrrigacoes() {
        return s_agendaIrrigacao.obterProximasIrrigacoes();
    }

    @PostMapping("/agendar")
    public String postAgendarIrrig(@RequestParam("datairrigacao") LocalDate dataIrrigacao,
                                   @RequestParam("horairrigacao") LocalTime horaIrrigacao,
                                   @RequestParam("intervalo") Integer intervalo,
                                   RedirectAttributes redirectAttributes) {

        // Valida a irrigação
        if (s_agendaIrrigacao.validaAgendaIrrigacao(dataIrrigacao, horaIrrigacao, intervalo)) {
            System.out.println("Irrigação agendada com sucesso!");
            redirectAttributes.addFlashAttribute("message", "Irrigação agendada com sucesso!");
        } else {
            System.out.println("Falha ao agendar irrigação.");
            redirectAttributes.addFlashAttribute("error", "Erro ao agendar a irrigação.");
        }

        // Redireciona para a página de próximas irrigações
        return "/agendar-irrigacao";
    }

    @GetMapping("/listar")
    public String getListaIrrigacoes(Model model) {
        List<M_Irrigacao> irrigacao = s_listaIrrigacao.listarIrrigacoes();
        model.addAttribute("irrigacao", irrigacao);
        return "/lista-irrigacao"; // Retorna o nome da view "lista-irrigacao.html"
    }

    @PostMapping("/atualizar")
    public String atualizarAtividade(@RequestParam("id") Long id,
                                     @RequestParam("datairrigacao") LocalDate dataIrrigacao,
                                     @RequestParam("horairrigacao") LocalTime horaIrrigacao,
                                     @RequestParam("intervalo") Integer intervalo) {
        try {
            // Chamada ao serviço
            boolean atualizado = s_agendaIrrigacao.atualizarAtividade(id, dataIrrigacao, horaIrrigacao, intervalo);

            if (atualizado) {
                return "/fragments/lista-irrigacao-fragment :: fragmentListaIrrigacao";
            } else {
                // Retorna status 404 caso o ID não seja encontrado ou não atualizado
                return ("Erro: irrigação não encontrada.");
            }
        } catch (Exception e) {
            // Retorna status 500 em caso de erro interno
            return ("Erro ao atualizar a irrigação: " + e.getMessage());
        }
    }

    @PostMapping("/deletar")
    @ResponseBody
    public String deletarAtividade(@RequestParam("id") Long id) {
        S_AgendaIrrigacao.deletarAtividade(id);
        return "/fragment/empty";
    }

    @GetMapping("/{id}")
    public ResponseEntity<M_Irrigacao> getIrrigacaoById(@PathVariable Long id) {
        M_Irrigacao irrigacao = r_irrigacao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Irrigação não encontrada."));
        return ResponseEntity.ok(irrigacao);
    }

}

