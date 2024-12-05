package com.hidroponia.hidroponia.service;

import com.fazecast.jSerialComm.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public class S_RecebeInfoArduino {
    private SerialPort serialPort;
    private BigDecimal temperature = BigDecimal.ZERO;
    private BigDecimal humidity = BigDecimal.ZERO;
    private BigDecimal dewPoint = BigDecimal.ZERO;
    private boolean dataReady = false;

    public S_RecebeInfoArduino(String portName) {
        connect(portName); // Tenta conectar ao criar o objeto
    }

    // Método para conectar à porta serial
    private void connect(String portName) {
        serialPort = SerialPort.getCommPort(portName);
        System.out.println("Tentando conectar na porta: " + portName);

        serialPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10000, 0);

        if (!serialPort.openPort()) {
            System.out.println("Erro ao abrir a porta serial.");
        } else {
            System.out.println("Conexão estabelecida com sucesso!");
        }
    }

    // Método para verificar se a conexão está ativa
    public boolean isConnected() {
        return serialPort != null && serialPort.isOpen();
    }

    // Método para desconectar da porta serial
    public void disconnect() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("Porta serial desconectada.");
        }
    }

    // Outros métodos como receiveData(), parseValue(), etc. permanecem os mesmos
    public void receiveData() {
        if (serialPort == null || !serialPort.isOpen()) {
            System.out.println("Porta serial não aberta, tentando reconectar...");
            return;
        }

        try (BufferedReader input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()))) {
            resetData();

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
                        break;
                    } else {
                        System.out.println("Dados incompletos, aguardando...");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro na comunicação serial: " + e.getMessage());
        }
    }

    // Métodos auxiliares para lidar com os dados
    private void resetData() {
        temperature = BigDecimal.ZERO;
        humidity = BigDecimal.ZERO;
        dewPoint = BigDecimal.ZERO;
        dataReady = false;
    }

    private BigDecimal parseValue(String line) {
        try {
            return new BigDecimal(line.split(": ")[1].trim());
        } catch (Exception e) {
            System.out.println("Erro ao interpretar valor: " + line);
            return BigDecimal.ZERO;
        }
    }

    // Getters para os dados
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
