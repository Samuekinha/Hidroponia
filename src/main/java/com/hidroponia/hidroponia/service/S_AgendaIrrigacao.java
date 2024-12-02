package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.model.M_Resultado;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
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

    public Boolean validaAgendaIrrigacao(LocalDate dataIrrigacao, LocalTime horaIrrigacao, Integer intervalo) {
        Boolean podeAgendar = true;
        List<String> mensagens = new ArrayList<>();

        try {
            // Validações iniciais de campos nulos ou intervalo inválido
            if (dataIrrigacao == null || horaIrrigacao == null || intervalo == null || intervalo < 1 || intervalo > 30) {
                podeAgendar = false;
                mensagens.add("Algum dos campos está vazio ou o intervalo é menor que 1 ou maior que 30.");
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
                m_irrigacao.setConcluida(false);
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

    public List<M_Irrigacao> obterProximasIrrigacoes() {
        LocalDate dataAtual = LocalDate.now();
        LocalTime horaAtual = LocalTime.now();

        // Obtém e retorna as próximas 5 irrigações, já ordenadas por data e hora
        List<M_Irrigacao> todasIrrigacoes = r_irrigacao.findNextIrrigacoes(dataAtual, horaAtual);
        return todasIrrigacoes.stream().limit(5).collect(Collectors.toList());
    }

    public Boolean atualizarAtividade(Long id, LocalDate dataIrrigacao, LocalTime horaIrrigacao, Integer intervalo) {
        // Busca a entidade pelo ID
        Optional<M_Irrigacao> optionalIrrigacao = r_irrigacao.findById(id);

        if (optionalIrrigacao.isPresent()) {
            M_Irrigacao irrigacao = optionalIrrigacao.get();

            // Atualiza os valores
            irrigacao.setDataIrrigacao(dataIrrigacao);
            irrigacao.setHoraIrrigacao(horaIrrigacao);
            irrigacao.setIntervalo(intervalo);

            // Salva as mudanças no banco de dados
            r_irrigacao.save(irrigacao);

            return true;
        } else {
            return false;
        }
    }


    public static Boolean deletarAtividade(Long id) {
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

    //inicio pesadelo
    public M_Resultado validaAgendaAvanc(LocalDate dataIrrigacao, LocalTime horaIrrigacao,
                                         Integer intervalo, LocalTime HoraIrrigacaoAvanc,
                                         int diaAvanc, int mesAvanc) {
        M_Resultado m_resultado = new M_Resultado();
        m_resultado.addMensagem("Iniciou.");
        boolean podeAgendar = true;

        try {

            int HoraFormat = HoraIrrigacaoAvanc.getHour();
            int MinFormat = HoraIrrigacaoAvanc.getMinute();
            HoraFormat = (HoraFormat * 60) + MinFormat;
            LocalTime agora = LocalTime.now();

            // Validações iniciais de campos nulos ou intervalo inválido
            if (dataIrrigacao == null || intervalo == null || intervalo < 1 || intervalo > 30) {
                podeAgendar = false;
                m_resultado.setAlerta("Algum dos campos está vazio ou o intervalo é menor que 1 ou maior que 30.");
            }

            // Validações de data e hora
            if (dataIrrigacao.isBefore(LocalDate.now())) {
                podeAgendar = false;
                m_resultado.setAlerta("A data de irrigação não pode ser no passado.");
            } else if (dataIrrigacao.isEqual(LocalDate.now()) && horaIrrigacao.isBefore(LocalTime.now())) {
                podeAgendar = false;
                m_resultado.setAlerta("A hora de irrigação não pode ser no passado para o dia atual.");
            }

            //criação da lista de irrigações desejada
            for (int i = 0; i < mesAvanc; i++) { //percorre qtde meses

                for (int j = 0; j < diaAvanc; j++) { //percorre qtde dias

                    //coiseia a primeira irrigação
                    int testeArrumar = 0;
                    int testeArrumarAss = 0;

                    if (!agora.isAfter(horaIrrigacao)){ //garante que a irrigação não é no passado

                        //conta para saber se é de um dia ou vai sobrepor
                        boolean irrigaProximoDia;
                        LocalTime irrigaHoraFim = horaIrrigacao.plusMinutes(intervalo);

                        int tempoLimite = (23*60) + 59;
                        int horaAvancValue = horaIrrigacao.getHour();
                        int minutoAvancValue = horaIrrigacao.getMinute();
                        int totalAvancValue = (horaAvancValue * 60) + minutoAvancValue;

                        for (int k = totalAvancValue; k <= tempoLimite ; k = k+(intervalo+1)) {
                            //cria as irrigas a partir da data de agora

                            if (!irrigaHoraFim.isAfter(horaIrrigacao)) {//descobre se esta sobrepondo
                                //variaveis aq poe no fim Sobrepost
                                irrigaProximoDia = true;

                                LocalDate dataAtualSobrepost = dataIrrigacao.minusDays(1); // Dia anterior
                                LocalTime horaAtualSobrepost = horaIrrigacao;

                                // Buscar a última irrigação do dia anterior (já q tem o bglh do minus days ali)
                                Optional<M_Irrigacao> ultimaIrrigacaoAnterior = r_irrigacao.findLastIrrigacaoBefore(dataAtualSobrepost, horaAtualSobrepost);

                                if (ultimaIrrigacaoAnterior.isPresent()) {
                                    M_Irrigacao ultimaIrrigacao = ultimaIrrigacaoAnterior.get();

                                    // Calculando o horário final da última irrigação do dia anterior
                                    LocalTime horaInicioAnterior = ultimaIrrigacao.getHoraIrrigacao();
                                    int intervaloAnterior = ultimaIrrigacao.getIntervalo(); // Em minutos
                                    LocalTime horaFimAnterior = horaInicioAnterior.plusMinutes(intervaloAnterior);

                                    // Transformando o horário final em minutos totais do dia
                                    int minutosTotaisFimAnterior = horaFimAnterior.getHour() * 60 + horaFimAnterior.getMinute();

                                    // Transformando o horário atual em minutos totais
                                    int minutosTotaisInicioAtual = horaAtualSobrepost.getHour() * 60 + horaAtualSobrepost.getMinute();

                                    // Comparando os horários
                                    if (minutosTotaisInicioAtual > minutosTotaisFimAnterior) {
                                        // Horário válido, pode prosseguir -> fecho dboas pode mandar bala

                                        Optional<M_Irrigacao> m_irrigacao = r_irrigacao.findLastIrrigacaoBefore(dataIrrigacao, horaIrrigacao);
                                        //descobre o intervalo da ultima irrigação de hoje
                                        Integer intervaloAnteriorSobrepost = m_irrigacao.get().getIntervalo();

                                        for (int l = ((-intervaloAnteriorSobrepost) - 1); l <= intervalo; l++) {
                                            // descob com o interval se tem uma outra irriga nesse horario
                                            LocalTime horaVerificada = horaIrrigacao.plusMinutes(l);

                                            List<M_Irrigacao> irrigacoesNoMesmoHorario = r_irrigacao.findByDataIrrigacaoAndHoraIrrigacao(dataIrrigacao, horaVerificada);
                                            //puxa a ultima irrigação que tem no dia

                                            if (!irrigacoesNoMesmoHorario.isEmpty()) {
                                                if (l < 0) {
                                                    m_resultado.addMensagem("Já existe uma irrigação cujo intervalo passará pelo horário: " + horaIrrigacao);
                                                } else {
                                                    m_resultado.addMensagem("Já existe uma irrigação que começará entre o horário: " + horaIrrigacao + " e " + horaIrrigacao.plusMinutes(intervalo));
                                                }
                                                podeAgendar = false;
                                                m_resultado.setSucesso(false);
                                                break;
                                            }

                                            //fazer salvamento no banco caso de certo avisar no console
                                            if (podeAgendar) {
                                                M_Irrigacao salvarNoBanco = new M_Irrigacao();
                                                salvarNoBanco.setDataIrrigacao(dataIrrigacao.plusDays(j-1));
                                                testeArrumar = intervalo;
                                                salvarNoBanco.setHoraIrrigacao(LocalTime.now().plusMinutes(testeArrumar));
                                                testeArrumar = testeArrumar+intervalo;
                                                salvarNoBanco.setDataRegistro(LocalDateTime.now());
                                                salvarNoBanco.setIntervalo(intervalo);
                                                salvarNoBanco.setConcluida(false);
                                                salvarNoBanco.getRegistroAutomatico(1);

                                                r_irrigacao.save(salvarNoBanco);
                                                m_resultado.setSucesso(true);
                                            } else {
                                                m_resultado.addMensagem("Não foi possivel agendar a irrigação de: < " + dataIrrigacao + " > e hora: < " + horaIrrigacao + " >" );
                                                m_resultado.setSucesso(false);
                                            }

                                        }

                                        podeAgendar = true;
                                    } else {
                                        // Conflito com a irrigação do dia anterior -> irrigação de ontem conflitooooooooooooo
                                        m_resultado.addMensagem("Conflito com a irrigação do dia anterior que termina às < " + horaFimAnterior + " > batendo com o inicio de: < " + horaIrrigacao + " >");
                                        podeAgendar = false;
                                        m_resultado.setSucesso(false);
                                    }
                                } else {
                                    // Não há irrigação no dia anterior, pode prosseguir
                                    m_resultado.addMensagem("Nenhuma irrigação encontrada no dia anterior. Agendamento permitido.");
                                    podeAgendar = true;
                                }


                            } else {
                                irrigaProximoDia = false;

                                Optional<M_Irrigacao> m_irrigacao = r_irrigacao.findLastIrrigacaoBefore(dataIrrigacao, horaIrrigacao);
                                //descobre o intervalo da ultima irrigação de hoje

                                if (m_irrigacao.isPresent()){
                                    M_Irrigacao irrigacaoAnterior = m_irrigacao.get(); // Obtemos o objeto M_Irrigacao
                                    int intervaloAnterior = irrigacaoAnterior.getIntervalo(); // Acessamos o intervalo

                                    for (int l = ((-intervaloAnterior) - 1); l <= intervalo; l++) {
                                        // descob com o interval se tem uma outra irriga nesse horario
                                        LocalTime horaVerificada = horaIrrigacao.plusMinutes(l);

                                        List<M_Irrigacao> irrigacoesNoMesmoHorario = r_irrigacao.findByDataIrrigacaoAndHoraIrrigacao(dataIrrigacao, horaVerificada);
                                        //puxa a ultima irrigação que tem no dia

                                        if (!irrigacoesNoMesmoHorario.isEmpty()) {
                                            if (l < 0) {
                                                m_resultado.addMensagem("Já existe uma irrigação cujo intervalo passará pelo horário: " + horaIrrigacao);
                                            } else {
                                                m_resultado.addMensagem("Já existe uma irrigação que começará entre o horário: " + horaIrrigacao + " e " + horaIrrigacao.plusMinutes(intervalo));
                                            }
                                            podeAgendar = false;
                                            m_resultado.setSucesso(false);
                                            break;
                                        }

                                        //fazer salvamento no banco caso de certo avisar no console
                                        if (podeAgendar) {

                                            //ajusta pra um bagulho ir recebendo essas coisas pra no fim so salvar todos
                                            //de uma vez pra caso dar erro ele não deixar salvar tudo até resolver
                                            //as pendencias

                                            M_Irrigacao salvarNoBanco = new M_Irrigacao();
                                            salvarNoBanco.setDataIrrigacao(dataIrrigacao.plusDays(j));


                                            LocalTime horaIrrigacaoParaSalvar = horaIrrigacao.plusMinutes(testeArrumar);
                                            testeArrumar = horaAvancValue + testeArrumarAss;
                                            salvarNoBanco.setHoraIrrigacao(horaIrrigacaoParaSalvar);
                                            testeArrumarAss = (testeArrumarAss + 1) + horaAvancValue;

                                            salvarNoBanco.setDataRegistro(LocalDateTime.now());
                                            salvarNoBanco.setIntervalo(intervalo);
                                            salvarNoBanco.setConcluida(false);
                                            salvarNoBanco.getRegistroAutomatico(1);

                                            r_irrigacao.save(salvarNoBanco);
                                            m_resultado.setSucesso(true);

                                        } else {
                                            m_resultado.addMensagem("Não foi possivel agendar a irrigação de: < " + dataIrrigacao + " > e hora: < " + horaIrrigacao + " >" );
                                            m_resultado.setSucesso(false);
                                        }

                                    }

                                }

                            }

                        }

                    } else{
                        m_resultado.setAlerta("A primeira irrigação não pode ser no passado.");
                        m_resultado.setSucesso(false);
                        break;
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            m_resultado.setAlerta("Operação encerrada por erro desconhecido.");
            m_resultado.setSucesso(false);
        }

        String alerta = m_resultado.getAlerta();
        List<String> mensagem = m_resultado.getMensagem();

        return new M_Resultado(podeAgendar, alerta, mensagem.toString());
    }

}