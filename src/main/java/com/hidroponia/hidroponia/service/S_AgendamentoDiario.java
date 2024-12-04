package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.model.M_IrrigacaoPattern;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import com.hidroponia.hidroponia.repository.R_IrrigacaoPattern;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class S_AgendamentoDiario {

    private static final Logger logger = LoggerFactory.getLogger(S_AgendamentoDiario.class);
    private final R_IrrigacaoPattern r_iPattern;
    private final R_Irrigacao r_irrigacao;

    private boolean jaSalvouHoje;

    public S_AgendamentoDiario(R_IrrigacaoPattern rIPattern, R_Irrigacao r_irrigacao) {
        r_iPattern = rIPattern;
        this.r_irrigacao = r_irrigacao;
    }

    @Async
    @Scheduled(fixedRate = 60000) // Executa a cada 60 segundos
    public void agendarIrrigacoesDiarias() {
        logger.info("Iniciando tentativa de agendamento diário.");

        if (jaSalvouHoje || LocalTime.now().isBefore(LocalTime.of(23, 56))) {
            logger.info("Já foi salvo hoje ou ainda não está na hora de agendar.");
            return;
        }

        try {
            List<M_IrrigacaoPattern> padroes = r_iPattern.findAll();

            if  (!jaSalvouHoje){
                for (M_IrrigacaoPattern padrao : padroes) {
                    if (!padrao.getActive()) continue;

                    LocalTime proximoHorario = LocalTime.of(0, 0);
                    int intervalo = padrao.getIntervalo();

                    long proximoHorarioSoma = 0;

                    while (proximoHorarioSoma < 1439) {
                        M_Irrigacao irrigacao = new M_Irrigacao();
                        irrigacao.setDataRegistro(LocalDateTime.now());
                        irrigacao.setDataIrrigacao(LocalDate.now().plusDays(1));
                        irrigacao.setIntervalo(intervalo);

                        irrigacao.setHoraIrrigacao((LocalTime.ofSecondOfDay(proximoHorarioSoma * 60)).plusMinutes(intervalo));

                        if (padrao.getComentario() != null) {
                            irrigacao.setComentario(padrao.getComentario());
                        }

                        proximoHorarioSoma = proximoHorarioSoma + intervalo;
                    }
                }


                List<M_Irrigacao> mIrrigacaos = r_irrigacao.findByDataIrrigacao(LocalDate.now().plusDays(1));
                if  (!mIrrigacaos.isEmpty()){
                    jaSalvouHoje = true;
                } else {
                    jaSalvouHoje = false;
                }
            }


        } catch (Exception e) {
            logger.error("Erro durante o agendamento diário: ", e);
        }
    }

}
