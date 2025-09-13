package com.hermes.hermes.repository;

import com.hermes.hermes.domain.model.sinistro.Sinistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SinistroRepository extends JpaRepository<Sinistro, Long> {

    List<Sinistro> findAllByCliente_IdIs(Long id);
}
