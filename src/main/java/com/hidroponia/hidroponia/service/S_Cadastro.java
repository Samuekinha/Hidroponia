package com.hidroponia.hidroponia.service;

import com.hidroponia.hidroponia.model.M_Resultado;
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
    public M_Resultado validaCadastro(String username, String senha, String confSenha, String email) {
        boolean podeSalvar = true;
        M_Resultado m_resultado =  new M_Resultado();
        M_Usuario m_usuario = new M_Usuario();

        try {
            // Aqui você pode adicionar a lógica para validar os dados
            if (username == null || senha == null || email == null || !senha.equalsIgnoreCase(confSenha)) {
                podeSalvar = false; // Retorna false se algum campo estiver vazio
                m_resultado.setAlerta("Algum dos campos está sem vazio.");
            }
        }
        catch (Exception e){
            podeSalvar = false;
            m_resultado.setAlerta("Erro desconhecido.");
        }

        if (podeSalvar){
            m_usuario.setUsername(username);
            m_usuario.setSenha(senha);
            m_usuario.setEmail(email);

            m_usuario = r_usuario.save(m_usuario);
        }

        String menssagem = m_resultado.getAlerta();

        return new M_Resultado(podeSalvar, menssagem);
    }



}