package com.hermes.hermes.repository;

import com.hermes.hermes.domain.model.sinistro.Sinistro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinistroRepository extends JpaRepository<Sinistro, Long> {
}
