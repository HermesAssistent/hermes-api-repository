package com.hermes.hermes.repository;

import com.hermes.hermes.domain.model.oficina.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    List<Orcamento> findBySinistroId(Long sinistroId);
    List<Orcamento> findByOficinaId(Long oficinaId);
    List<Orcamento> findBySinistroClienteId(Long clienteId);
}
