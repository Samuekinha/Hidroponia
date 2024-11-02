package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.service.S_AgendaIrrigacao;
import com.hidroponia.hidroponia.service.S_Login;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class C_CadastrarIrrigacao {

    private final S_AgendaIrrigacao s_agendaIrrigacao;

    public C_CadastrarIrrigacao(S_AgendaIrrigacao s_agendaIrrigacao) {
        this.s_agendaIrrigacao = s_agendaIrrigacao;
    }

    @GetMapping("/agendar-irrigacao")
    public String getIrrig(){
        return "/agendar-irrigacao";
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
