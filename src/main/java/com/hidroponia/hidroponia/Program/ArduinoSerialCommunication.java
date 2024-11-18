package com.hidroponia.hidroponia.Program;

import com.fazecast.jSerialComm.*;
import com.hidroponia.hidroponia.model.M_Irrigacao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public class ArduinoSerialCommunication {

    private SerialPort serialPort;

    // Variáveis para armazenar os valores
    private BigDecimal temperature = BigDecimal.ZERO;
    private BigDecimal humidity = BigDecimal.ZERO;
    private BigDecimal dewPoint = BigDecimal.ZERO;

    // Variável para verificar se os dados estão prontos
    private boolean dataReady = false;

    public static void main(String[] args) {
        ArduinoSerialCommunication arduino = new ArduinoSerialCommunication();
        arduino.initialize();
    }

    public void initialize() {
        // Lista as portas disponíveis
        SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length == 0) {
            System.out.println("Nenhuma porta serial encontrada.");
            return;
        }

        // Seleciona a primeira porta encontrada
        serialPort = ports[0];
        System.out.println("Conectando na porta: " + serialPort.getSystemPortName());

        // Configura a porta serial
        serialPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10000, 0);

        if (!serialPort.openPort()) {
            System.out.println("Erro ao abrir a porta serial.");
            return;
        }

        try (BufferedReader input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()))) {
            resetData();
            System.out.println("Conexão estabelecida! Aguardando dados...");

            String line;
            for (int i = 0; i < 4; i++) {
                boolean confirmaParada = false;
                while ((line = input.readLine()) != null) {
                    if (line.contains("Temperatura Celcius:")) {
                        temperature = parseValue(line);
                    } else if (line.contains("Umidade Relativa:")) {
                        humidity = parseValue(line);
                    } else if (line.contains("Ponto de Orvalho:")) {
                        dewPoint = parseValue(line);
                    }
                    if (line.equals("---FIM---")) {
                        // Verifica se todos os dados foram recebidos
                        if (temperature.compareTo(BigDecimal.ZERO) > 0 &&
                                humidity.compareTo(BigDecimal.ZERO) > 0 &&
                                dewPoint.compareTo(BigDecimal.ZERO) > 0) {
                            dataReady = true;
                            System.out.println("Dados recebidos:");
                            System.out.println("Temperatura: " + temperature);
                            System.out.println("Umidade: " + humidity);
                            System.out.println("Ponto de Orvalho: " + dewPoint);
                            confirmaParada = true; // Sai do loop após a leitura única
                            break;
                        } else {
                            System.out.println("Dados incompletos, aguardando...");
                        }
                    }
                }
                if (confirmaParada){
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Erro na comunicação serial: " + e.getMessage());
        }
    }

    private BigDecimal parseValue(String line) {
        try {
            System.out.println("Processando linha: " + line); // Log da entrada
            return new BigDecimal(line.split(": ")[1].trim());
        } catch (Exception e) {
            System.out.println("Erro ao interpretar valor: " + line + " -> " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    // Resetando os dados
    private void resetData() {
        temperature = BigDecimal.ZERO;
        humidity = BigDecimal.ZERO;
        dewPoint = BigDecimal.ZERO;
        dataReady = false;
    }

    // Métodos GET para acessar as variáveis
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
