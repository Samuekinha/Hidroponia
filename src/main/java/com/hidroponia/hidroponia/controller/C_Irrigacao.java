package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import com.hidroponia.hidroponia.service.S_AgendaIrrigacao;

import com.hidroponia.hidroponia.service.S_ListaIrrigacao;
import jakarta.servlet.http.HttpSession;
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
    public String mostrarProximasIrrigacoes(HttpSession session,
                                            Model model) {

        String username = (String) session.getAttribute("username");
        String nivel = (String) session.getAttribute("nivel");


        if (username != null) {

            if (!nivel.equals("USER")) {
                List<M_Irrigacao> proximasIrrigacoes = s_agendaIrrigacao.obterProximasIrrigacoes();
                model.addAttribute("proximasIrrigacoes", proximasIrrigacoes);
                model.addAttribute("message", "Bem-vindo, " + username + "!");
                return "/agendar-irrigacao"; // Nome do arquivo HTML
            } else {
                model.addAttribute("message", "Bem-vindo! Faça login.");
            }

        } else {
            model.addAttribute("message", "Usuario não autorizado.");
            return "redirect:/";
        }
        return null;
    }

    //lista de 5 proximas irriga
    @GetMapping("/agendarLista")
    @ResponseBody
    public List<M_Irrigacao> listarProximasIrrigacoes(HttpSession session,
                                                      Model model) {

        String username = (String) session.getAttribute("username");
        String nivel = (String) session.getAttribute("nivel");

        if (username != null) {
            if (!nivel.equals("USER")) {

                model.addAttribute("message", "Bem-vindo, " + username + "!");
                return s_agendaIrrigacao.obterProximasIrrigacoes(); // Nome do arquivo HTML

            } else {
                model.addAttribute("message", "Usuario não autorizado.");
            }
        } else {
            model.addAttribute("message", "Bem-vindo! Faça login.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autenticado");
        }
        return null;
    }

    //agendar irrigações
    @PostMapping("/agendar")
    public String postAgendarIrrig(@RequestParam("datairrigacao") LocalDate dataIrrigacao,
                                   @RequestParam("horairrigacao") LocalTime horaIrrigacao,
                                   @RequestParam("intervalo") Integer intervalo,
                                   Model model,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("username");

        if (username != null) {

            // Valida a irrigação
            if (s_agendaIrrigacao.validaAgendaIrrigacao(dataIrrigacao, horaIrrigacao, intervalo)) {
                System.out.println("Irrigação agendada com sucesso!");
                redirectAttributes.addFlashAttribute("message", "Irrigação agendada com sucesso!");
            } else {
                System.out.println("Falha ao agendar irrigação.");
                redirectAttributes.addFlashAttribute("error", "Erro ao agendar a irrigação.");
            }
            // Redireciona para a página de próximas irrigações
            return "redirect:/irrigacao/agendar";

        } else {
            return "redirect:/";
        }
    }

    //listar irrigações
    @GetMapping("/listar")
    public String getListaIrrigacoes(Model model,
                                     HttpSession session) {

        String username = (String) session.getAttribute("username");

        if (username != null) {

            List<M_Irrigacao> irrigacao = s_listaIrrigacao.listarIrrigacoesDesc();
            model.addAttribute("irrigacao", irrigacao);
            return "/lista-irrigacao"; // Retorna o nome da view "lista-irrigacao.html"

        } else {
            return "redirect:/";
        }

    }

    @PostMapping("/atualizar")
    public String atualizarAtividade(@RequestParam("id") Long id,
                                     @RequestParam("datairrigacao") LocalDate dataIrrigacao,
                                     @RequestParam("horairrigacao") LocalTime horaIrrigacao,
                                     @RequestParam("intervalo") Integer intervalo,
                                     HttpSession session,
                                     Model model) {

        String username = (String) session.getAttribute("username");

        if (username != null) {


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

        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/deletar")
    public String deletarAtividade(@RequestParam("id") Long id,
                                   HttpSession session,
                                   Model model) {

        String username = (String) session.getAttribute("username");
        String nivel = (String) session.getAttribute("nivel");

        if (username != null) {

            S_AgendaIrrigacao.deletarAtividade(id);
            return "/fragments/empty";

        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<M_Irrigacao> getIrrigacaoById(@PathVariable Long id,
                                                        HttpSession session,
                                                        Model model) {

        String username = (String) session.getAttribute("username");

        if (username != null) {
            model.addAttribute("message", "Bem-vindo, " + username + "!");

            M_Irrigacao irrigacao = r_irrigacao.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Irrigação não encontrada."));
            return ResponseEntity.ok(irrigacao);

        } else {
            model.addAttribute("message", "Bem-vindo! Faça login.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não autenticado");
        }

    }

}

