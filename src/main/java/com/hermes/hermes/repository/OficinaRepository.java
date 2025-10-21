package com.hermes.hermes.repository;

import com.hermes.hermes.domain.model.oficina.Oficina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OficinaRepository extends JpaRepository<Oficina, Long> {
    List<Oficina> findByEspecialidadesContaining(String especialidade);
}
