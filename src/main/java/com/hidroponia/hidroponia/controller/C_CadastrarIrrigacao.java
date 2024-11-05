package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import com.hidroponia.hidroponia.service.S_AgendaIrrigacao;
import com.hidroponia.hidroponia.service.S_Login;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "agendar-irrigacao"; // Nome da sua view
    }

    @PostMapping("/agendar-irrigacao")
    public String postAgendarIrrig(@RequestParam("datairrigacao") LocalDate dataIrrigacao,
                                   @RequestParam("horairrigacao") LocalTime horaIrrigacao,
                                   @RequestParam ("intervalo") Integer intervalo){
        if (s_agendaIrrigacao.validaAgendaIrrigacao(dataIrrigacao, horaIrrigacao, intervalo)){
            System.out.println("chique");
        }else{
            System.out.println("ruim");
        }

        return null;
    }

}
