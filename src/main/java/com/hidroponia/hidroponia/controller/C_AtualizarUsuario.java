package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.service.S_Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class C_AtualizarUsuario {

    private final S_Usuario s_usuario;

    public C_AtualizarUsuario(S_Usuario s_usuario) {
        this.s_usuario = s_usuario;
    }

    @GetMapping("//{id}")
    public String mostrarFormularioAtualizacao(@PathVariable Long id, Model model) {
        M_Usuario usuario = s_usuario.buscarUsuarioPorId(id);  // Buscar o usuário pelo ID
        model.addAttribute("usuario", usuario);  // Passar o usuário para o modelo
        return "/usuario/atualizarusuario";  // Nome da view Thymeleaf do formulário de edição
    }

    @PostMapping("/atualizarusuario")
    public String atualizarUsuario(@RequestParam("id") Long id,
                                   @RequestParam("username") String username,
                                   @RequestParam("senha") String senha,
                                   @RequestParam("email") String email,
                                   Model model) {



        M_Usuario usuario = s_usuario.buscarUsuarioPorId(id);
        usuario.setUsername(username);
        usuario.setEmail(email);

        // Criptografar a senha antes de salvar
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuario.setSenha(encoder.encode(senha));

        boolean sucesso = s_usuario.atualizarUsuario(id, username, senha, email);
        if (sucesso) {
            return "redirect:/home";  // Redireciona para a lista de usuários após atualização
        } else {
            model.addAttribute("error", "Erro ao atualizar o usuário.");
            return "usuario/atualizarusuario";  // Retorna com erro
        }
    }
}
