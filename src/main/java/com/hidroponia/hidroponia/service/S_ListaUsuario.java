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


    public boolean excluirUsuario(Long id) {
        if (r_usuario.existsById(id)) {
            r_usuario.deleteById(id);
            System.out.println("Usuário com ID " + id + " foi excluído com sucesso.");
            return true; // Usuário excluído com sucesso
        }

        return false;
    }
    public M_Usuario buscarUsuarioPorId(Long id) {
        return r_usuario.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
