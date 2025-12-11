package com.hermes.hermes.framework.orcamento.repository;

import com.hermes.hermes.framework.orcamento.domain.model.Orcamento;
import com.hermes.hermes.framework.orcamento.domain.enums.StatusOrcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
    
    List<Orcamento> findBySinistroId(Long sinistroId);

    @Query("SELECT o FROM Orcamento o WHERE o.sinistro.cliente.id = :clienteId")
    List<Orcamento> findBySinistroClienteId(@Param("clienteId") Long clienteId);
    
    List<Orcamento> findByStatus(StatusOrcamento status);
}
