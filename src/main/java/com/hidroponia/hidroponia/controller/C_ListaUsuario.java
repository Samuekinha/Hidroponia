package com.hidroponia.hidroponia.controller;

import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.service.S_Cadastro;
import com.hidroponia.hidroponia.service.S_ListaUsuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class C_ListaUsuario {

    private final S_ListaUsuario s_listaUsuario;

    public C_ListaUsuario(S_ListaUsuario s_listaUsuario) {
        this.s_listaUsuario = s_listaUsuario;
    }

    @GetMapping("/usuario/lista-usuarios")
    public String getListaUsuarios(Model model) {
        List<M_Usuario> usuarios = s_listaUsuario.listarUsuarios(); // Método que busca todos os usuários

        return "fragments/lista-usuario"; // Retorna o nome da view "lista.html"
    }
}
