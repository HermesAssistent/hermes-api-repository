package com.hermes.hermes.framework.sinistro.service;


import com.hermes.hermes.framework.exception.NotFoundException;
import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.sinistro.domain.strategy.SinistroStrategy;
import com.hermes.hermes.framework.exception.BusinessException;
import com.hermes.hermes.framework.exception.InvalidResourceStateException;
import com.hermes.hermes.framework.sinistro.repository.SinistroRepository;
import com.hermes.hermes.instancias.automotivo.repository.SinistroAutomotivoRepository;
import com.hermes.hermes.instancias.residencial.repository.SinistroResidencialRepository;
import com.hermes.hermes.instancias.transporte.repository.SinistroTransporteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SinistroService {
    private final SinistroFactory sinistroFactory;
    private final SinistroRepository sinistroRepository;
    private final GeocodingService geocodingService;
    private final SinistroAutomotivoRepository sinistroAutomotivoRepository;
    private final SinistroResidencialRepository sinistroResidencialRepository;
    private final SinistroTransporteRepository sinistroTransporteRepository;

    public SinistroBase criar(TipoSinistro tipo, Map<String, Object> dados) {
        SinistroStrategy strategy = sinistroFactory.getStrategy(tipo);
        if (strategy == null)
            throw new BusinessException("Tipo de sinistro não suportado");

        SinistroBase sinistro = strategy.criarSinistro(dados, geocodingService);

        return sinistroRepository.save(sinistro);
    }

    public SinistroBase criar(String tipoString, Map<String, Object> dados) {
        TipoSinistro tipo = TipoSinistro.fromString(tipoString);
        return criar(tipo, dados);
    }

    public List<SinistroBase> findAll() {
        return sinistroRepository.findAll();
    }

    public List<SinistroBase> findByClienteId(String id) {
        try {
            Long clienteId = Long.parseLong(id);
            return sinistroRepository.findAllByCliente_IdIs(clienteId);
        } catch (NumberFormatException e) {
            throw new InvalidResourceStateException("ID do cliente inválido: " + id);
        }
    }

    public SinistroBase buscarPorId(Long id, TipoSinistro tipo) {
        log.info("Buscando sinistro com ID: {} do tipo: {}", id, tipo);

        if (id == null) {
            throw new InvalidResourceStateException("ID do sinistro não fornecido");
        }

        return switch (tipo) {
            case TipoSinistro.AUTOMOTIVO ->
                    sinistroAutomotivoRepository.findById(id)
                            .orElseThrow(() -> new NotFoundException("Sinistro automotivo não encontrado com ID: " + id));

            case TipoSinistro.RESIDENCIAL ->
                    sinistroResidencialRepository.findById(id)
                            .orElseThrow(() -> new NotFoundException("Sinistro residencial não encontrado com ID: " + id));

            case TipoSinistro.TRANSPORTE ->
                    sinistroTransporteRepository.findById(id)
                            .orElseThrow(() -> new NotFoundException("Sinistro de carga não encontrado com ID: " + id));
            default -> throw new BusinessException("Tipo de sinistro não suportado: " + tipo);
        };
    }
}
