package com.hermes.hermes.framework.sinistro.repository;

import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SinistroRepository extends JpaRepository<SinistroBase, Long> {
    List<SinistroBase> findAllByCliente_IdIs(Long id);
}
