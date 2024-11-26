package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.repository.R_Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class S_Cadastro {

    private final R_Usuario r_usuario;
    private final BCryptPasswordEncoder passwordEncoder;

    public S_Cadastro(R_Usuario r_usuario, BCryptPasswordEncoder passwordEncoder) {
        this.r_usuario = r_usuario;
        this.passwordEncoder = passwordEncoder;
    }

    // Método para validar o cadastro
    public boolean validaCadastro(String username, String senha, String confSenha, String email) {
        boolean podeSalvar = true;
        M_Usuario m_usuario = new M_Usuario();

        try {
            // Lógica de validação
            if (username == null || senha == null || email == null || !senha.equals(confSenha)) {
                podeSalvar = false; // Retorna false se algum campo estiver inválido
            }
        } catch (Exception e) {
            podeSalvar = false;
        }

        if (podeSalvar) {
            m_usuario.setUsername(username);
            m_usuario.setSenha(passwordEncoder.encode(senha));
            m_usuario.setEmail(email);

            r_usuario.save(m_usuario); // Salva o usuário no banco de dados
            System.out.println("Cadastro realizado com sucesso!");
        }

        return podeSalvar;
    }
}
