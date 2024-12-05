package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import com.hidroponia.hidroponia.model.M_irrigacaoStatus;
import com.hidroponia.hidroponia.repository.R_Irrigacao;
import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class S_EnviaIrrigacao {

    private static final Logger logger = LoggerFactory.getLogger(S_EnviaIrrigacao.class);
    private final R_Irrigacao r_irrigacao;
    private M_Irrigacao ultimaIrrigacao = null;
    private M_Irrigacao m_irrigacao = new M_Irrigacao();
    private final M_irrigacaoStatus status = new M_irrigacaoStatus();
    private SerialPort serialPort;
    private BigDecimal temperature = BigDecimal.ZERO;
    private BigDecimal humidity = BigDecimal.ZERO;
    private BigDecimal dewPoint = BigDecimal.ZERO;
    private boolean dataReady = false;
    private ScheduledExecutorService scheduler;

    public S_EnviaIrrigacao(R_Irrigacao r_irrigacao) {
        this.r_irrigacao = r_irrigacao;
        status.setCountdownSegundos(null);
        status.setIrrigacaoAtualData(null);
        status.setIrrigacaoAtualHora(null);
        conectar("COM3");
    }

    public M_irrigacaoStatus getStatusAtual() {
        return this.status;
    }

    @Scheduled(fixedRate = 10000)
    public void checkNextIrrigationScheduled() {
        logger.info("Verificando próxima irrigação programada...");
        try {
            LocalDate dataAtual = LocalDate.now();
            LocalTime horaAtual = LocalTime.now().withNano(0);

            Optional<M_Irrigacao> irrigacaoFutura = r_irrigacao.findNextIrrigacaoToday(dataAtual, horaAtual);

            if (irrigacaoFutura.isPresent()) {
                m_irrigacao = irrigacaoFutura.get();
                LocalTime horaIrrigacao = m_irrigacao.getHoraIrrigacao();
                Integer segundosDiferenca = (int) Duration.between(horaAtual, horaIrrigacao).getSeconds();

                if (ultimaIrrigacao == null || !ultimaIrrigacao.getId().equals(m_irrigacao.getId())) {
                    ultimaIrrigacao = m_irrigacao;

                    status.setIrrigacaoAtualHora(m_irrigacao.getHoraIrrigacao());
                    status.setIrrigacaoAtualData(m_irrigacao.getDataIrrigacao());
                    status.setCountdownSegundos(segundosDiferenca);

                    if (scheduler != null && !scheduler.isShutdown()) {
                        scheduler.shutdownNow();
                    }
                    countDown(segundosDiferenca);
                }
            } else {
                logger.info("Nenhuma irrigação futura encontrada.");
            }
        } catch (Exception e) {
            logger.error("Erro ao verificar irrigação programada.", e);
        }
    }

    public void countDown(Integer intervaloTempo) {
        Integer startTime = intervaloTempo;
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(new Runnable() {
            int currentTime = startTime;

            @Override
            public void run() {
                if (currentTime <= 0) {
                    logger.info("Iniciando irrigação...");
                    try {
                        if (!isConnected()) {
                            conectar("COM3");
                        }

                        int intervalo = m_irrigacao.getIntervalo();
                        logger.info("Enviando comando ao Arduino: {} segundos de irrigação.", intervalo);
                        enviarComando(intervalo);

                        receiveData();

                        if (isDataReady()) {
                            ultimaIrrigacao.setTemperatura(getTemperature());
                            ultimaIrrigacao.setUmidade(getHumidity());
                            ultimaIrrigacao.setPontoOrvalho(getDewPoint());

                            r_irrigacao.save(ultimaIrrigacao);
                            logger.info("Dados salvos com sucesso.");
                        } else {
                            logger.error("Falha ao obter dados do Arduino.");
                        }
                    } catch (Exception e) {
                        logger.error("Erro durante irrigação.", e);
                    } finally {
                        status.setCountdownSegundos(null);
                        status.setIrrigacaoAtualHora(null);
                        status.setIrrigacaoAtualData(null);

                        if (isConnected()) {
                            disconnect();
                        }

                        if (scheduler != null && !scheduler.isShutdown()) {
                            scheduler.shutdown();
                        }
                    }
                } else {
                    currentTime--;
                    status.setCountdownSegundos(currentTime);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void conectar(String portName) {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10000, 0);

        if (!serialPort.openPort()) {
            logger.error("Erro ao abrir porta serial.");
        }
    }

    private boolean isConnected() {
        return serialPort != null && serialPort.isOpen();
    }

    private void disconnect() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
    }

    private void enviarComando(int intervalo) {
        if (serialPort != null && serialPort.isOpen()) {
            try {
                serialPort.getOutputStream().write((intervalo + "\n").getBytes());
                serialPort.getOutputStream().flush();
            } catch (Exception e) {
                logger.error("Erro ao enviar comando para o Arduino.", e);
            }
        }
    }

    private void receiveData() {
        if (serialPort == null || !serialPort.isOpen()) {
            logger.error("Porta serial não está aberta.");
            return;
        }

        // Tempo máximo para tentar obter os dados (6 segundos)
        long startTime = System.currentTimeMillis();
        long timeout = 6000; // 6 segundos

        try (BufferedReader input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()))) {
            resetData();

            while (System.currentTimeMillis() - startTime < timeout) {
                try {
                    String line;
                    while ((line = input.readLine()) != null) {
                        if (line.contains("Temperatura Celcius:")) {
                            temperature = parseValue(line);
                        } else if (line.contains("Umidade Relativa:")) {
                            humidity = parseValue(line);
                        } else if (line.contains("Ponto de Orvalho:")) {
                            dewPoint = parseValue(line);
                        }

                        if (line.equals("---FIM---")) {
                            dataReady = temperature.compareTo(BigDecimal.ZERO) > 0 &&
                                    humidity.compareTo(BigDecimal.ZERO) > 0 &&
                                    dewPoint.compareTo(BigDecimal.ZERO) > 0;

                            if (dataReady) {
                                logger.info("Dados lidos com sucesso!");
                                return; // Dados lidos, sai do método
                            } else {
                                logger.warn("Dados incompletos. Continuando...");
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Erro ao ler dados da porta serial. Tentando novamente...", e);
                }

                // Aguarda antes de tentar novamente
                Thread.sleep(1000); // 1 segundo
            }

            logger.error("Timeout ao tentar obter dados do Arduino após {} segundos.", timeout / 1000);
        } catch (Exception e) {
            logger.error("Erro ao acessar a porta serial.", e);
        }
    }

    private void resetData() {
        temperature = BigDecimal.ZERO;
        humidity = BigDecimal.ZERO;
        dewPoint = BigDecimal.ZERO;
        dataReady = false;
        logger.info("Dados resetados para valores iniciais.");
    }

    private BigDecimal parseValue(String line) {
        try {
            return new BigDecimal(line.split(": ")[1].trim());
        } catch (Exception e) {
            logger.error("Erro ao interpretar valor: {}", line);
            return BigDecimal.ZERO;
        }
    }

    private boolean isDataReady() {
        return dataReady;
    }

    private BigDecimal getTemperature() {
        return temperature;
    }

    private BigDecimal getHumidity() {
        return humidity;
    }

    private BigDecimal getDewPoint() {
        return dewPoint;
    }
}
