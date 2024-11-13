package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class S_ListaIrrigacao {

    private final R_Irrigacao r_irrigacao;

    public S_ListaIrrigacao(R_Irrigacao r_irrigacao) {
        this.r_irrigacao = r_irrigacao;
    }

    // Método para buscar irrigação por página
    public List<M_Irrigacao> listarIrrigacoesPorPagina(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return r_irrigacao.findAll(pageable).getContent();
    }

    // Método para buscar irrigação pelo ID
    public M_Irrigacao findById(Long id) {
        Optional<M_Irrigacao> irrigacao = r_irrigacao.findById(id);
        return irrigacao.orElse(null); // Retorna o objeto ou null se não encontrado
    }

    // Método para atualizar uma irrigação
    public M_Irrigacao update(Long id, M_Irrigacao updatedData) {
        return r_irrigacao.findById(id).map(irrigacao -> {
            irrigacao.setDataIrrigacao(updatedData.getDataIrrigacao());
            irrigacao.setHoraIrrigacao(updatedData.getHoraIrrigacao());
            irrigacao.setIntervalo(updatedData.getIntervalo());
            return r_irrigacao.save(irrigacao);
        }).orElse(null); // Retorna null se o ID não foi encontrado
    }

    // Método para deletar uma irrigação pelo ID
    public boolean deleteById(Long id) {
        if (r_irrigacao.existsById(id)) {
            r_irrigacao.deleteById(id);
            return true; // Retorna true se deletado com sucesso
        } else {
            return false; // Retorna false se o ID não foi encontrado
        }
    }
}
