package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Irrigacao;
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

    public S_EnviaIrrigacao(R_Irrigacao r_irrigacao) {
        this.r_irrigacao = r_irrigacao;
    }

    @Scheduled(fixedRate = 10000)
    @Async
    public CompletableFuture<Void> checkNextIrrigationScheduled() {
        logger.info("Método checkNextIrrigationScheduled() foi chamado com sucesso.");
        try {
            LocalDate dataAtual = LocalDate.now();
            LocalTime horaAtual = LocalTime.now().withNano(0);  // Remover nanosegundos

            logger.info("Data atual: {}", dataAtual);
            logger.info("Hora atual: {}", horaAtual);

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

    public static void countDown(Integer intervaloTempo) {
        Integer startTime = intervaloTempo;  // Começa a contagem em intervalo-1 segundos

        // Cria um novo scheduler para o countdown
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Define o countdown
        currentCountdown = new Runnable() {
            int currentTime = startTime;

            @Override
            public void run() {
                if (currentTime == 0) {
                    logger.info("Iniciando irrigação via Arduino.");
                    // Aqui você pode chamar o método para enviar dados para o Arduino
                    // O código de comunicação com o Arduino vai aqui

                    // Após iniciar a irrigação, o contador deve ser pausado ou resetado
                    scheduler.shutdownNow();  // Cancela o countdown
                } else {
                    logger.info("Contagem: {} segundos", currentTime);
                    currentTime--; // Decrementa o tempo a cada segundo
                }
            }
        };

        // Inicia o countdown
        scheduler.scheduleAtFixedRate(currentCountdown, 0, 1, TimeUnit.SECONDS);  // Começa imediatamente e repete a cada 1 segundo
    }
}