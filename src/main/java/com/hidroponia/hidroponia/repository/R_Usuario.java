package com.hidroponia.hidroponia.repository;

import com.hidroponia.hidroponia.model.M_Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface R_Usuario extends JpaRepository<M_Usuario, Long> {
    @Query(value = "SELECT * FROM usuario WHERE username = :username", nativeQuery = true)
    M_Usuario findByNome(@Param("username") String username);
}

























