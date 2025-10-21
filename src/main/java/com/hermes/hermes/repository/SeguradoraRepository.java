package com.hermes.hermes.repository;

import com.hermes.hermes.domain.model.seguradora.Seguradora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeguradoraRepository extends JpaRepository<Seguradora, Long> {
    Optional<Seguradora> findByCnpj(String cnpj);

    Optional<Seguradora> findByIdAndAtivoIsTrue(Long id);
}
