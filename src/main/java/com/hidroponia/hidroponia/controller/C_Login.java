package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.service.S_Login;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLOutput;

import static java.lang.System.*;

@Controller
public class C_Login {

    private final S_Login s_login;

    // Injeta o serviço no controlador através do construtor
    public C_Login(S_Login s_login) {
        this.s_login = s_login;
    }

    @GetMapping("/login")
    public String getLogin(){
        return "index";
    }

    @PostMapping("/login")
    public String postlogin(@RequestParam("username") String username,
                            @RequestParam("senha") String senha,
                            Model model) {

        System.out.println("Requisição recebida: " + username);
        // Se o login for bem-sucedido
        if (s_login.validaLogin(username, senha)) {
            model.addAttribute("logo");
            return "index";  // Página "home.html" será renderizada
        } else {
            model.addAttribute("lasco", "Senha incorreta!");
              // Página "index.html" será renderizada
            return "redirect:/index";
        }
    }
}
