package com.hermes.hermes.instancias.residencial.repository;

import com.hermes.hermes.instancias.residencial.domain.model.SinistroResidencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SinistroResidencialRepository extends JpaRepository<SinistroResidencial, Long> {
}
