package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.repository.R_Usuario;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class S_Login {

    private final R_Usuario r_usuario;

    public S_Login(R_Usuario rUsuario) {
        r_usuario = rUsuario;
    }

    public Boolean validaLogin(String username, String senha) {
        M_Usuario usuario = r_usuario.findByNome(username);
        System.out.println("Usuario encontrado: " + (usuario != null ? usuario.getUsername() : "null"));
        System.out.println("Senha fornecida: " + senha);
        System.out.println("Senha armazenada no banco: " + (usuario != null ? usuario.getSenha() : "null"));

        if (usuario != null) {
            return true;
        } else{
            return false;
        }
    }
}


