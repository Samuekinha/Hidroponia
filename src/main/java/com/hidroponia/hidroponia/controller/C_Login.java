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
    public String login(HttpSession session, Model model) {

        // Verifica se o usuário está logado (tem o atributo "username" na sessão)
        String username = (String) session.getAttribute("username");

        if (username != null) {
            // Se o usuário já estiver logado, redireciona para a home (ou página inicial)
            model.addAttribute("message", "Você já está logado, faça logout para logar denovo.");
            return "redirect:/";  // Redireciona para a página de início ou qualquer outra página que você preferir
        }

        return "index";  // Nome da página de login no seu template Thymeleaf

    }

    @PostMapping("/login")
    public String postlogin(@RequestParam("username") String username,
                            @RequestParam("senha") String senha,
                            HttpSession session,  // Adicionamos a sessão para armazenar as informações
                            Model model) {

        if (s_login.validaLogin(username, senha)) {
            // Sucesso no login, armazena o nome de usuário na sessão
            session.setAttribute("username", username);

            // Redireciona para a home após login
            return "redirect:/";
        } else {
            // Falha no login, retorna para a página de login com mensagem de erro
            model.addAttribute("error", "Credenciais inválidas.");
            return "login";  // Exibe novamente a página de login
        }
    }

}


