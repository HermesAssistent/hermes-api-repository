package com.hermes.hermes.repository.sinistro;

import com.hermes.hermes.domain.model.sinistro.SinistroBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SinistroRepository extends JpaRepository<SinistroBase, Long> {
    List<SinistroBase> findAllByCliente_IdIs(Long id);
}
