package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.model.M_IrrigacaoPattern;
import com.hidroponia.hidroponia.model.M_Resultado;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class S_AgendaIrrigacao {

    private static R_Irrigacao r_irrigacao;

    public S_AgendaIrrigacao(R_Irrigacao r_irrigacao) {
        this.r_irrigacao = r_irrigacao;
    }

    public M_Resultado validaAgendaIrrigacao(LocalTime horaIrrigacao, Integer intervalo) {
        Boolean podeAgendar = true;
        List<String> mensagens = new ArrayList<>();
        M_Resultado m_resultado = new M_Resultado();

        try {

            // Validações iniciais de campos nulos ou intervalo inválido
            if (horaIrrigacao == null || intervalo == null || intervalo < 1 || intervalo > 30) {
                podeAgendar = false;
                mensagens.add("Algum dos campos está vazio ou o intervalo é menor que 1 ou maior que 30.");
            }

            // Verificação de conflito com outras irrigações
            for (int i = -intervalo; i <= intervalo; i++) {
                LocalTime horaVerificada = horaIrrigacao.plusMinutes(i);

                List<M_Irrigacao> irrigacoesNoMesmoHorario = r_irrigacao.findByDataIrrigacaoAndHoraIrrigacao(LocalDate.now(), horaVerificada);
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

                m_irrigacao.setDataIrrigacao(LocalDate.now());
                m_irrigacao.setHoraIrrigacao(horaIrrigacao);
                m_irrigacao.setDataRegistro(LocalDateTime.now());
                m_irrigacao.setIntervalo(intervalo);
                m_irrigacao.setConcluida(false);
                r_irrigacao.save(m_irrigacao);
                m_resultado.setAlerta("Irrigação agendada!");
            }

            // Exibir mensagens de erro
            for (String msg : mensagens) {
                System.out.println(msg);
            }
        } catch (Exception e) {
            podeAgendar = false;
            e.printStackTrace(); // Para ajudar na depuração
        }

        String mensagem = m_resultado.getAlerta();

        return new M_Resultado(podeAgendar, mensagem);
    }

    public List<M_Irrigacao> obterProximasIrrigacoes() {
        LocalDate dataAtual = LocalDate.now();
        LocalTime horaAtual = LocalTime.now();

        // Obtém e retorna as próximas 5 irrigações, já ordenadas por data e hora
        List<M_Irrigacao> todasIrrigacoes = r_irrigacao.findNextIrrigacoes(dataAtual, horaAtual);
        return todasIrrigacoes.stream().limit(5).collect(Collectors.toList());
    }

    public M_Resultado atualizarAtividade(Long id, LocalTime horaIrrigacao, Integer intervalo) {
        // Busca a entidade pelo ID
        Optional<M_Irrigacao> optionalIrrigacao = r_irrigacao.findById(id);
        M_Resultado m_resultado = new M_Resultado();
        boolean funcionou;

        if (optionalIrrigacao.isPresent()) {
            M_Irrigacao irrigacao = optionalIrrigacao.get();

            // Atualiza os valores
            irrigacao.setDataIrrigacao(LocalDate.now());
            irrigacao.setHoraIrrigacao(horaIrrigacao);
            irrigacao.setIntervalo(intervalo);

            // Salva as mudanças no banco de dados
            r_irrigacao.save(irrigacao);

            funcionou = true;
        } else {
            m_resultado.setAlerta("Essa irrigação não foi encontrada ou não existe.");
            funcionou = false;
        }

        String mensagem = m_resultado.getAlerta();

        return new M_Resultado(funcionou, mensagem);
    }


    public static Boolean deletarIrrigacao(Long id) {
        // Busca a entidade pelo ID
        Optional<M_Irrigacao> optionalIrrigacao = r_irrigacao.findById(id);

        if (optionalIrrigacao.isPresent()) {
            M_Irrigacao irrigacao = optionalIrrigacao.get();

            // Salva as mudanças no banco de dados
            r_irrigacao.delete(irrigacao);

            return true;
        } else {
            return false;
        }
    }

    public Boolean salvarIrrigacoesPattern(Integer duracao, Integer intervalo, boolean concluida) {

        try {
            M_IrrigacaoPattern m_iPattern = new M_IrrigacaoPattern();

            if (concluida){
                m_iPattern.setIntervalo(intervalo);
                m_iPattern.setDuracao(duracao);
                m_iPattern.setActive(true);
            } else {
                return false;
            }

            System.out.println("Novo pattern foi salvo!");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //inicio pesadelo
    public M_Resultado validaAgendaAvanc(Integer duracao, LocalTime intervalo) {

        M_Resultado m_resultado = new M_Resultado();
        boolean concluida = true;

        try {

            if (duracao > 30 || duracao < 1) {
                m_resultado.setAlerta("O intervalo precisa estar entre 1 e 30");
                concluida = false;
            } else if (intervalo.isAfter(LocalTime.of(1, 30)) || intervalo.isBefore(LocalTime.of(0, 1))) {
                m_resultado.setAlerta("O intervalo entre uma irrigação e outra precisa ser entre 00:01 e 11:59 horas ");
                concluida = false;
            }

            //define uma hora limite para garantir a integridade do sistema
            LocalTime horaLimiteAgenda = LocalTime.of(23, 55);
            LocalTime horaMinimaAgenda = LocalTime.of(0, 5);

            if (!LocalTime.now().isAfter(horaLimiteAgenda) || !LocalTime.now().isBefore(horaMinimaAgenda)) {

                //chama funçao para armazenar os dados das irrigações
                int intervaloSum = ((intervalo.getHour() * 60) + intervalo.getMinute());
                if (salvarIrrigacoesPattern(duracao, intervaloSum, concluida)){
                    m_resultado.setAlerta("Novo pattern registrado e ativo!");
                } else {
                    m_resultado.setAlerta("Erro ao salvar novo pattern.");
                }

            } else {
                m_resultado.setAlerta("Aguarde até as 00:06 para voltar a manipular irrigações.");
                concluida = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            m_resultado.setAlerta("Operação encerrada por erro desconhecido.");
            concluida = false;
        }

        String alerta = m_resultado.getAlerta();

        return new M_Resultado(concluida, alerta);
    }

}