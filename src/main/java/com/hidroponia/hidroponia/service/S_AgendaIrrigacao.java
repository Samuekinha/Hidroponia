package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.model.M_Resultado;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import org.springframework.cglib.core.Local;
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
                                         Integer intervalo, LocalDate dataFim, LocalTime intervaloRecAvanc) {

        M_Resultado m_resultado = new M_Resultado();
        boolean podeAgendar = true;

        try {
            m_resultado.addMensagem("Iniciou.");

            if (dataIrrigacao == null || horaIrrigacao == null || intervalo == null || dataFim == null || intervaloRecAvanc == null){
                m_resultado.setAlerta("Todos os campos precisam estar preenchidos.");
            } else if (horaIrrigacao.isBefore(LocalTime.of(0, 1)) || horaIrrigacao.isAfter(LocalTime.of(23, 59))) {
                m_resultado.setAlerta("Hora da irrigação tem que ser entre 00:01 e 23:59");
            } else if (intervalo > 30 || intervalo < 1) {
                m_resultado.setAlerta("O intervalo precisa estar entre 1 e 30");
            } else if (intervaloRecAvanc.isAfter(LocalTime.of(11, 59)) || intervaloRecAvanc.isBefore(LocalTime.of(0, 1))) {
                m_resultado.setAlerta("O intervalo entre uma irrigação e outra precisa ser entre 00:01 e 11:59 horas ");
            } else if (intervaloRecAvanc.isBefore(LocalTime.ofSecondOfDay(intervalo * 60)) ||
                    intervaloRecAvanc.equals(LocalTime.ofSecondOfDay(intervalo * 60))){
                m_resultado.setAlerta("O intervalo entre irrigações não pode ser menor que a duração.");
            }

            boolean primeiroDiaPercorrendo = true;

            LocalDate i = dataIrrigacao;

            //começa os processos
            while (i.isBefore(dataFim) || i.isEqual(dataFim)) {
                m_resultado.setAlerta("entrou");
                //percorre os dias partindo da data que foi passada

                LocalTime horaDeAgora = LocalTime.now();

                //descobrir em inteiros a hora/min p/comparações
                int somaHoraMinAtual = (horaDeAgora.getHour() * 60) + horaDeAgora.getMinute();

                //o valor que da horas do dia todo
                int somaTotalHoraMinDiaTwenyFour = (24 * 60);

                //intervalo entre cada irrigação
                int intervaloRecAvancSoma = (intervaloRecAvanc.getHour() * 60) + intervaloRecAvanc.getMinute();

                int intervaloRecAvancAuxiliarTotal = (intervaloRecAvanc.getHour() * 60) + intervaloRecAvanc.getMinute();
                //pega o valor do intervalo total q deveria ter cada irrigação

                if (primeiroDiaPercorrendo){
                    //lógica sendo o primeiro dia

                    int intervaloParaUmMinDeCadaIrriga = 0;
                    //garante que se for o primeiro dia

                    while (somaHoraMinAtual < somaTotalHoraMinDiaTwenyFour) {
                        //enquanto a soma do horario de agr for menor que 1440 (minutos do dia todo) mantem no loop

                        if (intervaloParaUmMinDeCadaIrriga == 0){
                            //vai somar normalmente, a primeira do dia no primeiro dia percorrendo vai começar na hr certinha
                            somaHoraMinAtual = (somaHoraMinAtual + intervaloRecAvancSoma) ;
                            //faz com q as próximas tenham 1 minuto de intervalo para agendamento de cada uma ex: 23:30 + 20 -> 23:50
                            //23:51 próxima
                            intervaloParaUmMinDeCadaIrriga = intervaloParaUmMinDeCadaIrriga + 1;
                        } else {
                            //vai somar normalmente
                            somaHoraMinAtual = (somaHoraMinAtual + intervaloRecAvancSoma) ;
                        }

                        /*lógica pra descobrir se tem sobreposição de irrigações*/
                        LocalDate dataAtualSobrepost = dataIrrigacao.minusDays(1); // Dia anterior
                        LocalTime horaAtualSobrepost = horaIrrigacao;

                        // Buscar a última irrigação do dia anterior (já q tem o bglh do minus days ali)
                        Optional<M_Irrigacao> ultimaIrrigacaoAnterior = r_irrigacao.findLastIrrigacaoBefore(dataAtualSobrepost, horaAtualSobrepost);

                        if (ultimaIrrigacaoAnterior.isPresent()) {
                            M_Irrigacao ultimaIrrigacao = ultimaIrrigacaoAnterior.get();

                            // Calculando o horário final da última irrigação do dia anterior
                            LocalTime horaInicioAnterior = ultimaIrrigacao.getHoraIrrigacao();//aqui n é literal a hora (hh) e sim (hh:mm)
                            int intervaloAnterior = ultimaIrrigacao.getIntervalo(); // Em minutos
                            LocalTime horaFimAnterior = horaInicioAnterior.plusMinutes(intervaloAnterior);

                            // Transformando o horário final da irriga anterior em minutos totais do dia
                            int minutosTotaisFimAnterior = (horaFimAnterior.getHour() * 60) + horaFimAnterior.getMinute();

                            // Transformando o horário atual em minutos totais
                            int minutosTotaisInicioAtual = horaAtualSobrepost.getHour() * 60 + horaAtualSobrepost.getMinute();

                            // Comparando os horários, se o comeco da de hj for maior q fim da de ontem
                            if (minutosTotaisInicioAtual > minutosTotaisFimAnterior) {

                                Optional<M_Irrigacao> m_irrigacao = r_irrigacao.findLastIrrigacaoBefore(dataIrrigacao, horaIrrigacao);
                                //descobre o intervalo da ultima irrigação de hoje
                                Integer intervaloAnteriorSobrepost = m_irrigacao.get().getIntervalo();

                                for (int l = (-intervaloAnteriorSobrepost); l <= intervalo; l++) {
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
                                    }

                                    //fazer salvamento no banco caso de certo avisar no console
                                    if (podeAgendar) {

                                        //ajusta pra um bagulho ir recebendo essas coisas pra no fim so salvar todos
                                        //de uma vez pra caso dar erro ele não deixar salvar tudo até resolver
                                        //as pendencias

                                        M_Irrigacao salvarNoBanco = new M_Irrigacao();
                                        salvarNoBanco.setDataIrrigacao(dataIrrigacao);


                                        LocalTime horaIrrigacaoParaSalvar = horaIrrigacao.plusMinutes(intervaloRecAvancAuxiliarTotal);
                                        //horairrigacao recebe o intervalo de tempo q cada uma deve ter
                                        salvarNoBanco.setHoraIrrigacao(horaIrrigacaoParaSalvar);
                                        //aqui p baixo ela vai receber o intervalo dnv q ai vai ir aumentando ate chega no fim do while
                                        intervaloRecAvancAuxiliarTotal = intervaloRecAvancAuxiliarTotal + intervaloRecAvancAuxiliarTotal;

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

                                    podeAgendar = true;
                                }

                            } else {
                                // Conflito com a irrigação do dia anterior -> irrigação de ontem conflitooooooooooooo
                                m_resultado.addMensagem("Conflito com a irrigação do dia anterior que termina às < " + horaFimAnterior + " > batendo com o inicio de: < " + horaIrrigacao + " >");
                                podeAgendar = false;
                                m_resultado.setSucesso(false);
                            }
                        } else {
                            // Não há irrigação no dia anterior, pode prosseguirc com a logica

                            Optional<M_Irrigacao> m_irrigacao = r_irrigacao.findLastIrrigacaoBefore(dataIrrigacao, horaIrrigacao);
                            //descobre o intervalo da ultima irrigação de hoje
                            Integer intervaloAnteriorSobrepost = m_irrigacao.get().getIntervalo();

                            for (int l = (-intervaloAnteriorSobrepost); l <= intervalo; l++) {
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
                                }

                                //fazer salvamento no banco caso de certo avisar no console
                                if (podeAgendar) {

                                    //ajusta pra um bagulho ir recebendo essas coisas pra no fim so salvar todos
                                    //de uma vez pra caso dar erro ele não deixar salvar tudo até resolver
                                    //as pendencias

                                    M_Irrigacao salvarNoBanco = new M_Irrigacao();
                                    salvarNoBanco.setDataIrrigacao(dataIrrigacao);


                                    LocalTime horaIrrigacaoParaSalvar = horaIrrigacao.plusMinutes(intervaloRecAvancAuxiliarTotal);
                                    //horairrigacao recebe o intervalo de tempo q cada uma deve ter
                                    salvarNoBanco.setHoraIrrigacao(horaIrrigacaoParaSalvar);
                                    //aqui p baixo ela vai receber o intervalo dnv q ai vai ir aumentando ate chega no fim do while
                                    intervaloRecAvancAuxiliarTotal = intervaloRecAvancAuxiliarTotal + intervaloRecAvancAuxiliarTotal;

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

                                podeAgendar = true;
                            }

                        }

                    }

                    primeiroDiaPercorrendo = false;

                } else{ /* Começa a partir do segundo day bitch*/

                    //lógica sendo a partir do segundo dia

                    /*lógica pra descobrir se tem sobreposição de irrigações*/
                    LocalDate dataAtualSobrepost = dataIrrigacao.minusDays(1); // Dia anterior
                    LocalTime horaAtualSobrepost = horaIrrigacao;

                    // Buscar a última irrigação do dia anterior (já q tem o bglh do minus days ali)
                    Optional<M_Irrigacao> ultimaIrrigacaoAnterior = r_irrigacao.findLastIrrigacaoBefore(dataAtualSobrepost, horaAtualSobrepost);

                    if  (ultimaIrrigacaoAnterior.isPresent()){
                        M_Irrigacao ultimaIrrigacao = ultimaIrrigacaoAnterior.get();

                        // Calculando o horário que vai acabar a última irrigação do dia anterior
                        int horaFimAnterior = ((ultimaIrrigacao.getHoraIrrigacao().getHour()) * 60) +
                                (ultimaIrrigacao.getHoraIrrigacao().getMinute());
                        //aqui n é literal a hora (hh) e sim (hh:mm)
                        int intervaloAnterior = ultimaIrrigacao.getIntervalo(); // Em minutos
                        horaFimAnterior = horaFimAnterior + intervaloAnterior;
                        //soma a qtde de min de ontem + o intervalo (q ultrapassou o dia de ontem) e da pra usa agr

                        int minTotaisDeUmDia = 24 * 60;

                        long minutosUltrapassados = horaFimAnterior - minTotaisDeUmDia; //descobre qnts min q passou a mais

                        //logica pra saber se tem alguma irriga pelo meio das q tao sendo feita

                        while (minutosUltrapassados <= minTotaisDeUmDia){

                            LocalTime salvarDBHoraIrriga;
                            if (((minutosUltrapassados * 60) - 1 ) < 86400){ //passa para segundos oq era minutos
                                salvarDBHoraIrriga = (LocalTime.ofSecondOfDay(minutosUltrapassados * 60));
                            } else{
                                m_resultado.setAlerta("Erro ao salvar horario da irrigação, tempo excedente. Contate o suporte.");
                                break;
                            }

                            Optional<M_Irrigacao> m_irrigacao = r_irrigacao.findLastIrrigacaoBefore(i, salvarDBHoraIrriga);
                            //descobre o intervalo da ultima irrigação de hoje
                            Integer intervaloAnteriorSobrepost = m_irrigacao.get().getIntervalo();

                            for (int l = (-intervaloAnteriorSobrepost); l <= intervalo; l++) {
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
                                }

                                //fazer salvamento no banco caso de certo avisar no console
                                if (podeAgendar) {

                                    //ajusta pra um bagulho ir recebendo essas coisas pra no fim so salvar todos
                                    //de uma vez pra caso dar erro ele não deixar salvar tudo até resolver
                                    //as pendencias

                                    M_Irrigacao salvarNoBanco = new M_Irrigacao();

                                    salvarNoBanco.setDataIrrigacao(i);

                                    salvarNoBanco.setHoraIrrigacao(salvarDBHoraIrriga);
                                    //hora a partir da ideia que existe uma irriga q passou do horario do seu own dia

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

                                podeAgendar = true;
                            }

                            minutosUltrapassados = minutosUltrapassados + intervaloRecAvancSoma;
                        }


                    } else {
                        //lógica caso não tenha irrigações sobrepondo de ontem pr hoje

                        long minutosUltrapassados = 0;
                        int minTotaisDeUmDia = 60 * 24;

                        while (minutosUltrapassados <= minTotaisDeUmDia){

                            LocalTime salvarDBHoraIrriga;
                            if (((minutosUltrapassados * 60) - 1 ) < 86400){ //passa para segundos oq era minutos
                                salvarDBHoraIrriga = (LocalTime.ofSecondOfDay(minutosUltrapassados * 60));
                            } else{
                                m_resultado.setAlerta("Erro ao salvar horario da irrigação, tempo excedente. Contate o suporte.");
                                break;
                            }

                            Optional<M_Irrigacao> m_irrigacao = r_irrigacao.findLastIrrigacaoBefore(i, salvarDBHoraIrriga);
                            //descobre o intervalo da ultima irrigação de hoje
                            Integer intervaloAnteriorSobrepost = m_irrigacao.get().getIntervalo();

                            for (int l = (-intervaloAnteriorSobrepost); l <= intervalo; l++) {
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
                                }

                                //fazer salvamento no banco caso de certo avisar no console
                                if (podeAgendar) {

                                    //ajusta pra um bagulho ir recebendo essas coisas pra no fim so salvar todos
                                    //de uma vez pra caso dar erro ele não deixar salvar tudo até resolver
                                    //as pendencias

                                    M_Irrigacao salvarNoBanco = new M_Irrigacao();

                                    salvarNoBanco.setDataIrrigacao(i);

                                    salvarNoBanco.setHoraIrrigacao(salvarDBHoraIrriga);
                                    //hora a partir da ideia que existe uma irriga q passou do horario do seu own dia

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

                                podeAgendar = true;
                            }

                            minutosUltrapassados = minutosUltrapassados + intervaloRecAvancSoma;
                        }

                    }

                }

                i.plusDays(1);
            }
            m_resultado.setAlerta("passou reto");

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