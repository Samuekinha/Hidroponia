package com.hidroponia.hidroponia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class C_ListaIrrigacao {

    @GetMapping("/lista-irrigacao")
    public String getListaIrrig(){
        return "/lista-irrigacao";
    }
}
