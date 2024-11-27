package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.repository.R_Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class S_Cadastro {

    private final R_Usuario r_usuario;

    public S_Cadastro(R_Usuario rUsuario) {
        r_usuario = rUsuario;
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
            m_usuario.setSenha(senha);
            m_usuario.setEmail(email);

            r_usuario.save(m_usuario); // Salva o usuário no banco de dados
            System.out.println("Cadastro realizado com sucesso!");
        }

        return podeSalvar;
    }
}
