package com.hermes.hermes.instancias.transporte.repository;

import com.hermes.hermes.instancias.transporte.domain.model.SinistroTransporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SinistroTransporteRepository extends JpaRepository<SinistroTransporte, Long> {
}
