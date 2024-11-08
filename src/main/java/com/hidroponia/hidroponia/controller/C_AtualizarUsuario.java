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

    // Exibir o formulário de atualização com dados do usuário
    @GetMapping("/usuario/atualizarusuario/{id}")
    public String mostrarFormularioAtualizacao(@PathVariable Long id, Model model) {
        M_Usuario usuario = s_usuario.buscarUsuarioPorId(id);  // Buscar o usuário pelo ID
        model.addAttribute("usuario", usuario);  // Passar o usuário para o modelo
        return "/usuario/atualizarusuario";  // Nome da view Thymeleaf (arquivo HTML)
    }

    // Processar a atualização do usuário
    @PostMapping("/atualizarusuario")
    public String atualizarUsuario(@RequestParam("id") Long id,
                                   @RequestParam("username") String username,
                                   @RequestParam("senha") String senha,
                                   @RequestParam("conf_senha") String conf_senha,
                                   @RequestParam("email") String email,
                                   Model model) {

        if (!senha.equals(conf_senha)) {
            model.addAttribute("error", "As senhas não coincidem!");  // Exibir erro se as senhas não coincidirem
            return "atualizarusuario";
        }

        M_Usuario usuario = s_usuario.buscarUsuarioPorId(id);  // Buscar o usuário pelo ID
        usuario.setUsername(username);  // Atualiza o nome de usuário
        usuario.setEmail(email);

        usuario.setSenha(senha);

        boolean sucesso = s_usuario.atualizarUsuario(id, username, senha, email);  // Atualizar o usuário
        if (sucesso) {
            return "redirect:/home";  // Redirecionar para a lista de usuários após atualização
        } else {
            model.addAttribute("error", "Erro ao atualizar o usuário.");
            return "usuario/atualizarusuario";
        }
    }
}
