package com.hermes.hermes.instancias.transporte.repository;

import com.hermes.hermes.instancias.transporte.domain.model.SinistroCarga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SinistroCargaRepository extends JpaRepository<SinistroCarga, Long> {
}
