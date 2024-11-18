package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.service.S_AssyncSimulation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController  // Usando o controlador REST do Spring
public class C_AssyncSimulation {

    private static final Logger logger = LoggerFactory.getLogger(C_AssyncSimulation.class);

    @Autowired  // Injetando o serviço de irrigação
    private S_AssyncSimulation irrigationService;

    @GetMapping("/test-irrigation")
    public String testIrrigation() {
        logger.info("Iniciando agendamento de irrigação...");
        irrigationService.scheduleIrrigation(10, 30, 60);
        logger.info("Irrigação agendada (sem esperar terminar).");
        return "Irrigação agendada!";
    }
}
