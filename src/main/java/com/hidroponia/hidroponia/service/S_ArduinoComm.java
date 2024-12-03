package com.hidroponia.hidroponia.service;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;

@Service
public class S_ArduinoComm {

    private static final Logger logger = LoggerFactory.getLogger(S_ArduinoComm.class);
    private SerialPort serialPort;

    private BigDecimal temperature = BigDecimal.ZERO;
    private BigDecimal humidity = BigDecimal.ZERO;
    private BigDecimal dewPoint = BigDecimal.ZERO;
    private boolean dataReady = false;

    // Conecta na porta serial especificada
    public void conectar(String porta) {
        serialPort = SerialPort.getCommPort(porta);
        serialPort.setBaudRate(9600); // Taxa de transmissão padrão
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);

        if (serialPort.openPort()) {
            logger.info("Conectado ao Arduino na porta {}", porta);
        } else {
            logger.error("Não foi possível conectar ao Arduino na porta {}", porta);
        }
    }

    // Envia um comando para o Arduino
    public void enviarComando(String comando) {
        if (serialPort != null && serialPort.isOpen()) {
            try {
                serialPort.getOutputStream().write(comando.getBytes());
                serialPort.getOutputStream().flush();
                logger.info("Comando enviado: {}", comando);
            } catch (Exception e) {
                logger.error("Erro ao enviar comando para o Arduino", e);
            }
        } else {
            logger.error("A porta serial não está aberta.");
        }
    }

    // Lê dados do Arduino
    public void lerDados() {
        if (serialPort == null || !serialPort.isOpen()) {
            logger.error("A porta serial não está aberta.");
            return;
        }

        try (BufferedReader input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()))) {
            resetData();
            logger.info("Aguardando dados do Arduino...");

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
                    if (temperature.compareTo(BigDecimal.ZERO) > 0 &&
                            humidity.compareTo(BigDecimal.ZERO) > 0 &&
                            dewPoint.compareTo(BigDecimal.ZERO) > 0) {
                        dataReady = true;
                        logger.info("Dados recebidos: Temperatura: {}, Umidade: {}, Ponto de Orvalho: {}",
                                temperature, humidity, dewPoint);
                        break;
                    } else {
                        logger.warn("Dados incompletos, aguardando...");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao ler dados do Arduino", e);
        }
    }

    // Fecha a porta serial
    public void desconectar() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            logger.info("Porta serial desconectada.");
        }
    }

    private BigDecimal parseValue(String line) {
        try {
            logger.debug("Processando linha: {}", line);
            return new BigDecimal(line.split(": ")[1].trim());
        } catch (Exception e) {
            logger.error("Erro ao interpretar valor: {}", line, e);
            return BigDecimal.ZERO;
        }
    }

    private void resetData() {
        temperature = BigDecimal.ZERO;
        humidity = BigDecimal.ZERO;
        dewPoint = BigDecimal.ZERO;
        dataReady = false;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public BigDecimal getHumidity() {
        return humidity;
    }

    public BigDecimal getDewPoint() {
        return dewPoint;
    }

    public boolean isDataReady() {
        return dataReady;
    }
}
