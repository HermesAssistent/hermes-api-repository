package com.hermes.hermes.instancias.transporte.repository;

import com.hermes.hermes.instancias.transporte.domain.model.SinistroTransporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SinistroTransporteRepository extends JpaRepository<SinistroTransporte, Long> {
    List<SinistroTransporte> findAllByCliente_IdIs(Long clienteId);
    Long countByPerdaTotalTrue();
    Long countByCargaRecuperadaTrue();
    Long countByPossuiSeguroTrue();
    Long countByGravidadeEqualsIgnoreCase(String gravidade);
    List<SinistroTransporte> findByTransportadora(String transportadora);
    List<SinistroTransporte> findByTipoOcorrencia(String tipoOcorrencia);
}
