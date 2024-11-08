package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.repository.R_Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class S_Usuario {

    private final R_Usuario r_usuario;

    public S_Usuario(R_Usuario r_usuario) {
        this.r_usuario = r_usuario;
    }

    // Buscar usuário pelo ID
    public M_Usuario buscarUsuarioPorId(Long id) {
        return r_usuario.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // Atualizar o usuário no banco de dados
    public boolean atualizarUsuario(Long id, String username, String senha, String email) {
        M_Usuario usuario = r_usuario.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setUsername(username);  // Atualiza o nome de usuário
        usuario.setEmail(email);  // Atualiza o email

        // Criptografar a senha antes de salvar
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuario.setSenha(encoder.encode(senha));  // Criptografar a senha

        r_usuario.save(usuario);  // Salvar o usuário atualizado no banco de dados
        return true;
    }
}
