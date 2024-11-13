package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.service.S_ListaIrrigacao;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class C_ListaIrrigacao {

    private final S_ListaIrrigacao s_listaIrrigacao;

    public C_ListaIrrigacao(S_ListaIrrigacao s_listaIrrigacao) {
        this.s_listaIrrigacao = s_listaIrrigacao;
    }

    @GetMapping("/lista-irrigacao")
    public String getListaIrrigacoes(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     Model model) {
        List<M_Irrigacao> irrigacao = s_listaIrrigacao.listarIrrigacoesPorPagina(page, size);
        model.addAttribute("irrigacao", irrigacao);
        return "lista-irrigacao"; // Retorna o nome da view "lista-irrigacao.html"
    }

    // Endpoint para obter uma irrigação pelo ID
    @GetMapping("/irrigacoes/{id}")
    public ResponseEntity<M_Irrigacao> getIrrigacaoById(@PathVariable Long id) {
        M_Irrigacao irrigacao = s_listaIrrigacao.findById(id); // Método correto no serviço
        if (irrigacao != null) {
            return ResponseEntity.ok(irrigacao);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para atualizar uma irrigação
    @PutMapping("/irrigacoes/update/{id}")
    public ResponseEntity<M_Irrigacao> updateIrrigacao(@PathVariable Long id, @RequestBody M_Irrigacao updatedData) {
        M_Irrigacao updatedIrrigacao = s_listaIrrigacao.update(id, updatedData); // Método correto no serviço
        if (updatedIrrigacao != null) {
            return ResponseEntity.ok(updatedIrrigacao);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para deletar uma irrigação
    @DeleteMapping("/irrigacoes/delete/{id}")
    public ResponseEntity<Void> deleteIrrigacao(@PathVariable Long id) {
        boolean isDeleted = s_listaIrrigacao.deleteById(id); // Método delete ajustado
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
