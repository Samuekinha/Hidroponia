package com.hidroponia.hidroponia.service;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service  // Tornando a classe um componente do Spring
public class S_AssyncSimulation {

    private static final Logger logger = LoggerFactory.getLogger(S_AssyncSimulation.class);

    @Async  // Tornando o método assíncrono
    public void scheduleIrrigation(int hour, int minute, int duration) {
        logger.info("Iniciando irrigação às: " + hour + ":" + minute);

        try {
            // Simula um atraso (como se estivesse fazendo o trabalho de irrigação)
            Thread.sleep(5000);  // Simula a irrigação durando 5 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Irrigação concluída! Durou " + duration + " minutos.");
    }
}
