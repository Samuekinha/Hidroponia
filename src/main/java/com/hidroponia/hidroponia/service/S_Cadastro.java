package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Usuario;
import com.hidroponia.hidroponia.repository.R_Usuario;
import org.springframework.stereotype.Service;

@Service
public class S_Cadastro {

    private static R_Usuario r_usuario;


    public S_Cadastro(R_Usuario r_usuario) {
        this.r_usuario = r_usuario;
    }

    // Método para validar o cadastro
    public static boolean validaCadastro(String username, String senha, String confSenha, String email) {
        boolean podeSalvar = true;
        M_Usuario m_usuario = new M_Usuario();

        try {
            // Aqui você pode adicionar a lógica para validar os dados
            if (username == null || senha == null || email == null || !senha.equalsIgnoreCase(confSenha)) {
                podeSalvar = false; // Retorna false se algum campo estiver vazio
            }
        }
        catch (Exception e){
            podeSalvar = false;
        }

        if (podeSalvar){
            m_usuario.setUsername(username);
            m_usuario.setSenha(senha);
            m_usuario.setEmail(email);

            m_usuario = r_usuario.save(m_usuario);
            System.out.println("deu bao so confira");
        }

        return podeSalvar;
    }



}
