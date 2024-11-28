package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.service.S_Login;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/")
    public String home(HttpSession session,
                           Model model){

        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("message", "Bem-vindo, " + username + "!");
            return "redirect:/home";
        } else {
            model.addAttribute("message", "Bem-vindo! Faça login.");
            return "redirect:/login"; // Nome da página Thymeleaf
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidar a sessão
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(HttpSession session,
                        Model model) {

        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("message", "Bem-vindo, " + username + "!");
            return "redirect:/home";
        } else {
            model.addAttribute("message", "Bem-vindo! Faça login.");
            return "index"; // Nome da página Thymeleaf
        }
    }

    @PostMapping("/login")
    public String postlogin(@RequestParam("username") String username,
                            @RequestParam("senha") String senha,
                            HttpSession session,
                            Model model) {

        session.setAttribute("username", username);
        System.out.println("Requisição recebida: " + username);
        // Se o login for bem-sucedido
        if (s_login.validaLogin(username, senha)) {
            return "redirect:/";
        } else {
            // Página "index.html" será renderizada
            return "index";
        }
    }

}


