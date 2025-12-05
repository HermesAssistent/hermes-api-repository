package com.hermes.hermes.framework.seguradora.repository;

import com.hermes.hermes.framework.seguradora.domain.model.Seguradora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeguradoraRepository extends JpaRepository<Seguradora, Long> {
    Optional<Seguradora> findByCnpj(String cnpj);

    Optional<Seguradora> findByIdAndAtivoIsTrue(Long id);

    Optional<Seguradora> findByAtivoIsTrueAndUsuario_Id(Long id);

}
