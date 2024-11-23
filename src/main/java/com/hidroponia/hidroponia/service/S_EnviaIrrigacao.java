package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.model.M_irrigacaoStatus;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class S_EnviaIrrigacao {
    private static M_Irrigacao ultimaIrrigacao = null;  // Para armazenar o último estado da irrigação

    private static final Logger logger = LoggerFactory.getLogger(S_EnviaIrrigacao.class);

    private final R_Irrigacao r_irrigacao;

    private static ScheduledExecutorService scheduler; // Para armazenar o scheduler
    private static Runnable currentCountdown; // Para armazenar o runnable do countdown

    private M_irrigacaoStatus status = new M_irrigacaoStatus();

    public M_irrigacaoStatus getStatusAtual() {
        return this.status; // Nunca retorna nulo
    }

    public S_EnviaIrrigacao(R_Irrigacao r_irrigacao) {
        this.r_irrigacao = r_irrigacao;
        this.status = new M_irrigacaoStatus();
        // Inicialize valores padrão se necessário
        status.setCountdownSegundos(null);
        status.setIrrigacaoAtualData(null);
        status.setIrrigacaoAtualHora(null);
    }


    @Scheduled(fixedRate = 10000)
    public CompletableFuture<Void> checkNextIrrigationScheduled() {
        logger.info("Método checkNextIrrigationScheduled() foi chamado com sucesso.");
        try {
            LocalDate dataAtual = LocalDate.now();
            LocalTime horaAtual = LocalTime.now().withNano(0);  // Remover nanosegundos

            logger.info("Data at: {}", dataAtual);
            logger.info("Hora at: {}", horaAtual);

            Optional<M_Irrigacao> irrigacaoFutura = r_irrigacao.findNextIrrigacaoToday(dataAtual, horaAtual);

            if (!irrigacaoFutura.isEmpty()) {
                M_Irrigacao m_irrigacao = irrigacaoFutura.get();  // Pega a irrigação encontrada
                LocalTime horaIrrigacao = m_irrigacao.getHoraIrrigacao();  // Acessa o campo intervalo da irrigação encontrada
                horaAtual = LocalTime.now();

                Integer segundosDiferenca = Math.toIntExact(Duration.between(horaAtual, horaIrrigacao).getSeconds());

                // Verifica se a irrigação atual tem o mesmo ID da última
                if (ultimaIrrigacao == null || !ultimaIrrigacao.getId().equals(m_irrigacao.getId())) {
                    logger.info("Nova irrigação encontrada, iniciando countdown.");
                    ultimaIrrigacao = m_irrigacao;  // Atualiza a última irrigação encontrada

                    status.setIrrigacaoAtualHora(m_irrigacao.getHoraIrrigacao());
                    status.setIrrigacaoAtualData(m_irrigacao.getDataIrrigacao());
                    status.setCountdownSegundos(segundosDiferenca);

                    // Reinicia o countdown
                    if (scheduler != null && !scheduler.isShutdown()) {
                        scheduler.shutdownNow();  // Cancela o countdown anterior
                    }
                    countDown(segundosDiferenca);  // Inicia o novo countdown
                }
            } else {
                logger.info("Não há irrigação futura para o horário atual.");
            }
        } catch (Exception e) {
            logger.error("Erro ao executar irrigação agendada", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    public void countDown(Integer intervaloTempo) {
        Integer startTime = intervaloTempo;  // Começa a contagem em intervalo-1 segundos

        // Cria um novo scheduler para o countdown
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Define o countdown
        currentCountdown = new Runnable() {
            int currentTime = startTime;

            @Override
            public void run() {
                if (currentTime == 0 || currentTime == 1) {
                    logger.info("Iniciando irrigação via Arduino.");

                    // enviar informação pra começar a irrigação
                    //lembra de ajustar esse if pra ter tempo a mais pro arduino

                    status.setCountdownSegundos(currentTime);

                    // Após iniciar a irrigação, o contador deve ser pausado ou resetado
                    scheduler.shutdownNow();  // Cancela o countdown
                } else {
                    logger.info("Cont: {} s", currentTime);
                    currentTime = currentTime-2; // Decrementa o tempo a cada 2 segundos
                    status.setCountdownSegundos(currentTime);

                }
            }
        };

        // Inicia o countdown
        scheduler.scheduleAtFixedRate(currentCountdown, 0, 2, TimeUnit.SECONDS);  // Começa imediatamente e repete a cada 1 segundo
    }

}
