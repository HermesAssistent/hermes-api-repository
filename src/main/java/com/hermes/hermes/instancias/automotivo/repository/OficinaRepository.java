package com.hermes.hermes.instancias.automotivo.repository;

import com.hermes.hermes.instancias.automotivo.domain.model.Oficina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OficinaRepository extends JpaRepository<Oficina, Long> {
    List<Oficina> findByEspecialidadesContaining(String especialidade);
    List<Oficina> findBySeguradoras_Id(Long seguradoraId);
}
