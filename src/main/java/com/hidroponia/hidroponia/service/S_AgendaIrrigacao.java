package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class S_AgendaIrrigacao {
    private final R_Irrigacao r_irrigacao;

    public S_AgendaIrrigacao(R_Irrigacao r_irrigacao) {
        this.r_irrigacao = r_irrigacao;
    }

    public Boolean validaAgendaIrrigacao(LocalDate dataIrrigacao, LocalTime horaIrrigacao, Integer intervalo) {
        Boolean podeAgendar = true;
        List<String> mensagens = new ArrayList<>();

        try {
            // Validações iniciais de campos nulos ou intervalo inválido
            if (dataIrrigacao == null || horaIrrigacao == null || intervalo == null || intervalo < 1 || intervalo > 30) {
                podeAgendar = false;
                mensagens.add("Algum dos campos está vazio ou o intervalo é menor ou maior que 30.");
            }

            // Validações de data e hora
            if (dataIrrigacao.isBefore(LocalDate.now())) {
                podeAgendar = false;
                mensagens.add("A data de irrigação não pode ser no passado.");
            } else if (dataIrrigacao.isEqual(LocalDate.now()) && horaIrrigacao.isBefore(LocalTime.now())) {
                podeAgendar = false;
                mensagens.add("A hora de irrigação não pode ser no passado para o dia atual.");
            }

            // Verificação de conflito com outras irrigações
            for (int i = -intervalo; i <= intervalo; i++) {
                LocalTime horaVerificada = horaIrrigacao.plusMinutes(i);

                List<M_Irrigacao> irrigacoesNoMesmoHorario = r_irrigacao.findByDataIrrigacaoAndHoraIrrigacao(dataIrrigacao, horaVerificada);
                if (!irrigacoesNoMesmoHorario.isEmpty()) {
                    if (i < 0) {
                        mensagens.add("Já existe uma irrigação cujo intervalo passará pelo horário: " + horaIrrigacao);
                    } else {
                        mensagens.add("Já existe uma irrigação que começará entre o horário: " + horaIrrigacao + " e " + horaIrrigacao.plusMinutes(intervalo));
                    }
                    podeAgendar = false;
                    break;
                }
            }

            // Salvar no banco se não houver conflitos
            if (podeAgendar) {
                M_Irrigacao m_irrigacao = new M_Irrigacao();
                m_irrigacao.setDataIrrigacao(dataIrrigacao);
                m_irrigacao.setHoraIrrigacao(horaIrrigacao);
                m_irrigacao.setDataRegistro(LocalDateTime.now());
                m_irrigacao.setIntervalo(intervalo);
                r_irrigacao.save(m_irrigacao);
                return true;
            }

            // Exibir mensagens de erro
            for (String msg : mensagens) {
                System.out.println(msg);
            }
        } catch (Exception e) {
            podeAgendar = false;
            e.printStackTrace(); // Para ajudar na depuração
        }

        return false;
    }
}
