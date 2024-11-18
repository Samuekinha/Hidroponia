package com.hidroponia.hidroponia.Program;

import com.fazecast.jSerialComm.SerialPort;
import java.time.LocalDateTime;
import java.time.Duration;

public class IrrigationScheduler {
    private LocalDateTime nextIrrigationTime;
    private long timeRemaining;  // Tempo restante em milissegundos
    private SerialPort arduinoSerialPort;  // Porta serial para comunicação com o Arduino

    public IrrigationScheduler() {
        // Inicialização do próximo agendamento (exemplo)
        this.nextIrrigationTime = LocalDateTime.now().plusMinutes(10); // Exemplo
        this.timeRemaining = Duration.between(LocalDateTime.now(), nextIrrigationTime).toMillis();
        // Configuração do Arduino (porta e parâmetros devem ser ajustados conforme necessário)
        this.arduinoSerialPort = SerialPort.getCommPorts()[0];  // Ajuste para a porta serial correta
        arduinoSerialPort.openPort();  // Abre a porta serial
        arduinoSerialPort.setComPortParameters(9600, 8, 1, 0);  // Configura parâmetros da porta (baud rate, data bits, stop bits, parity)
    }

    public void start() {
        while (true) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= timeRemaining - 60000) {  // Faltando 1 minuto
                sendDataToArduino();
                // Atualiza o próximo agendamento (Exemplo: próximo agendamento em 30 minutos)
                this.nextIrrigationTime = LocalDateTime.now().plusMinutes(30);
                this.timeRemaining = Duration.between(LocalDateTime.now(), nextIrrigationTime).toMillis();
            }

            // Espera 1 minuto antes de verificar novamente
            try {
                Thread.sleep(60000);  // 1 minuto
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void sendDataToArduino() {
        // Enviar para o Arduino a quantidade de tempo restante até 10 segundos antes da irrigação
        long remainingTimeForArduino = Duration.between(LocalDateTime.now(), nextIrrigationTime.minusSeconds(10)).toMillis();
        System.out.println("Enviando para o Arduino: " + remainingTimeForArduino);

        // Converte o tempo para string e envia via porta serial
        String message = String.valueOf(remainingTimeForArduino) + "\n";  // Adiciona uma nova linha para separar as mensagens
        byte[] messageBytes = message.getBytes();  // Converte para bytes
        arduinoSerialPort.writeBytes(messageBytes, messageBytes.length);  // Envia os bytes para o Arduino
    }

    public static void main(String[] args) {
        new IrrigationScheduler().start();
    }
}
