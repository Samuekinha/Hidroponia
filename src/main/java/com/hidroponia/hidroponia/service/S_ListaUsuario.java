package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.repository.R_Usuario;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class S_ListaUsuario {

    private static R_Usuario r_usuario;


    public S_ListaUsuario(R_Usuario r_usuario) {
        this.r_usuario = r_usuario;
    }

    // Método para validar o cadastro
    public List<M_Usuario> listarUsuarios() {
        return r_usuario.findAll(); // Busca todos os usuários no banco
    }

}
