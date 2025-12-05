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
    
    /**
     * Busca orçamentos por ID do sinistro.
     */
    List<Orcamento> findBySinistroId(Long sinistroId);
    
    /**
     * Busca orçamentos por ID do prestador.
     */
    List<Orcamento> findByPrestadorId(Long prestadorId);
    
    /**
     * Busca orçamentos por cliente através do sinistro.
     */
    @Query("SELECT o FROM OrcamentoOficina o WHERE o.sinistro.cliente.id = :clienteId")
    List<Orcamento> findBySinistroClienteId(@Param("clienteId") Long clienteId);
    
    /**
     * Busca orçamentos por status.
     */
    List<Orcamento> findByStatus(StatusOrcamento status);
    
    /**
     * Busca orçamentos pendentes de um sinistro específico.
     */
    List<Orcamento> findBySinistroIdAndStatus(Long sinistroId, StatusOrcamento status);
    
    /**
     * Busca orçamentos de um prestador por status.
     */
    List<Orcamento> findByPrestadorIdAndStatus(Long prestadorId, StatusOrcamento status);
}
