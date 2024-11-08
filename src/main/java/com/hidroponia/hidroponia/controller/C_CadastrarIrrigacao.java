package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import com.hidroponia.hidroponia.service.S_AgendaIrrigacao;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class C_CadastrarIrrigacao {

    private final S_AgendaIrrigacao s_agendaIrrigacao;
    private final R_Irrigacao r_irrigacao;

    public C_CadastrarIrrigacao(S_AgendaIrrigacao s_agendaIrrigacao, R_Irrigacao rIrrigacao) {
        this.s_agendaIrrigacao = s_agendaIrrigacao;
        r_irrigacao = rIrrigacao;
    }

    @GetMapping("/agendar-irrigacao")
    public String mostrarProximasIrrigacoes(Model model) {
        List<M_Irrigacao> proximasIrrigacoes = s_agendaIrrigacao.obterProximasIrrigacoes();
        model.addAttribute("proximasIrrigacoes", proximasIrrigacoes);
        return "/agendar-irrigacao" +
                ""; // Retorna um fragmento específico
    }

    @PostMapping("/agendar-irrigacao")
    public String postAgendarIrrig(@RequestParam("datairrigacao") LocalDate dataIrrigacao,
                                   @RequestParam("horairrigacao") LocalTime horaIrrigacao,
                                   @RequestParam ("intervalo") Integer intervalo,
                                   RedirectAttributes redirectAttributes) {

        // Valida a irrigação
        if (s_agendaIrrigacao.validaAgendaIrrigacao(dataIrrigacao, horaIrrigacao, intervalo)){
            System.out.println("Irrigação agendada com sucesso!");
            redirectAttributes.addFlashAttribute("message", "Irrigação agendada com sucesso!");
        } else {
            System.out.println("Falha ao agendar irrigação.");
            redirectAttributes.addFlashAttribute("error", "Erro ao agendar a irrigação.");
        }

        // Redireciona para a página de próximas irrigações
        return "redirect:/agendar-irrigacao";
    }

}
