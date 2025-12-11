package com.hermes.hermes.instancias.residencial.repository;

import com.hermes.hermes.instancias.residencial.domain.model.SinistroResidencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SinistroResidencialRepository extends JpaRepository<SinistroResidencial, Long> {
    List<SinistroResidencial> findAllByCliente_IdIs(Long clienteId);
    Long countByEstruturaComprometidaTrue();
    Long countByHabitavelFalse();
    Long countByPossuiSeguroTrue();
    Long countByGravidadeEqualsIgnoreCase(String gravidade);
}
