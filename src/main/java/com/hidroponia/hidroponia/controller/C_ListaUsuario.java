package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.service.S_Cadastro;
import com.hidroponia.hidroponia.service.S_ListaUsuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class C_ListaUsuario {

    private final S_ListaUsuario s_listaUsuario;

    public C_ListaUsuario(S_ListaUsuario s_listaUsuario) {
        this.s_listaUsuario = s_listaUsuario;
    }

    @GetMapping("/lista-usuario")
    public String getListaUsuarios(Model model) {
        List<M_Usuario> usuarios = s_listaUsuario.listarUsuarios(); // Método que busca todos os usuários
        model.addAttribute("usuarios", usuarios);
        return "/usuario/lista-usuario"; // Retorna o nome da view "lista.html"
    }

    @PostMapping("/usuario/excluir/{id}")
    public String excluirUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean sucesso = s_listaUsuario.excluirUsuario(id);
        if (sucesso) {
            redirectAttributes.addFlashAttribute("mensagem", "Usuário excluído com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagem", "Erro ao excluir o usuário!");
        }
        return "redirect:/lista-usuario";
    }
}
