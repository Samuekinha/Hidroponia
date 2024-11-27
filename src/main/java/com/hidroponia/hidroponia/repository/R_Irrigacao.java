package com.hidroponia.hidroponia.repository;

import com.hidroponia.hidroponia.model.M_Irrigacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface R_Irrigacao extends JpaRepository<M_Irrigacao, Long> {
    // Se você deseja buscar irrigação por algum campo específico
    // Exemplo: buscar irrigação pelo ID
    Optional<M_Irrigacao> findById(Long id);

    // Se você tiver um método para buscar irrigação por data, por exemplo
    List<M_Irrigacao> findByDataIrrigacao(LocalDate dataIrrigacao); // Corrigido aqui

    List<M_Irrigacao> findByHoraIrrigacao(LocalTime horaIrrigacao); // Corrigido aqui

    @Query("SELECT i FROM M_Irrigacao i " +
            "WHERE i.dataIrrigacao = :dataIrrigacao AND i.horaIrrigacao = :horaIrrigacao") // Corrigido aqui
    List<M_Irrigacao> findByDataIrrigacaoAndHoraIrrigacao(@Param("dataIrrigacao") LocalDate dataIrrigacao,
                                                          @Param("horaIrrigacao") LocalTime horaIrrigacao);

    @Query("SELECT i FROM M_Irrigacao i " +
            "WHERE i.dataIrrigacao >= :dataAtual AND i.horaIrrigacao >= :horaAtual " +
            "ORDER BY i.dataIrrigacao, i.horaIrrigacao")
    List<M_Irrigacao> findNextIrrigacoes(@Param("dataAtual") LocalDate dataAtual, @Param("horaAtual") LocalTime horaAtual);

    @Query(value = "SELECT * FROM irrigacao i " +
            "WHERE i.data_irrigacao = :dataAtual " +
            "AND i.hora_irrigacao > :horaAtual " +
            "ORDER BY i.hora_irrigacao ASC LIMIT 1", nativeQuery = true)
    Optional<M_Irrigacao> findNextIrrigacaoToday(@Param("dataAtual") LocalDate dataAtual,
                                                 @Param("horaAtual") LocalTime horaAtual);

}