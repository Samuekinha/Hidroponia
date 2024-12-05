package com.hidroponia.hidroponia.service;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
public class S_ArduinoComm {

    private static final Logger logger = LoggerFactory.getLogger(S_ArduinoComm.class);
    private SerialPort serialPort;

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
    public void enviarComando(int intervaloMinutos) {
        if (serialPort != null && serialPort.isOpen()) {
            try {
                // Cria o comando com o intervalo, garantindo que não haja espaços ou novas linhas extras
                String comando = intervaloMinutos + "\n";
                comando = comando.trim(); // Remove espaços ou quebras de linha extras

                OutputStream outputStream = serialPort.getOutputStream();
                outputStream.write(comando.getBytes());
                outputStream.flush();
                logger.info("Comando enviado: {}", comando);
            } catch (IOException e) {
                logger.error("Erro ao enviar comando para o Arduino", e);
            }
        } else {
            logger.error("A porta serial não está aberta.");
        }
    }

    // Fecha a porta serial
    public void desconectar() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            logger.info("Porta serial desconectada.");
        }
    }

    // Método para verificar se a conexão está ativa
    public boolean isConnected() {
        return serialPort != null && serialPort.isOpen();
    }
}
