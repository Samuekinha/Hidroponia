package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.service.S_Cadastro;
import com.hidroponia.hidroponia.service.S_Login;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class C_Cadastro {

    private final S_Cadastro s_cadastro;

    // Injeta o serviço no controlador através do construtor
    public C_Cadastro(S_Cadastro s_cadastro) {
        this.s_cadastro = s_cadastro;
    }

    @GetMapping("/cadastrar")
    public String getCadastro(HttpSession session,
                              Model model){

        String username = (String) session.getAttribute("username");
        if (username != null) {
            return "/usuario/cadastrar";
        } else {
            model.addAttribute("message", "Bem-vindo! Faça login.");
            return "redirect:/login"; // Nome da página Thymeleaf
        }

    }

    @PostMapping("/cadastrar")
    public String postCadastro(@RequestParam("username") String username,
                               @RequestParam("senha") String senha,
                               @RequestParam("conf_senha") String conf_senha,
                               @RequestParam("email") String email,
                               HttpSession session,
                               Model model) {

        String name = (String) session.getAttribute("username");
        if (name != null) {
            if (s_cadastro.validaCadastro(username,senha,conf_senha,email).isSucesso()){
                return "redirect:/usuario/cadastrar";
            } else{
                model.addAttribute("message", "erro validando cadasstro");
                return "redirect:/usuario/cadastrar";
            }
        } else {
            model.addAttribute("message", "Bem-vindo! Faça login.");
            return "redirect:/login"; // Nome da página Thymeleaf
        }
        // Se o login for bem-sucedido

    }
}
