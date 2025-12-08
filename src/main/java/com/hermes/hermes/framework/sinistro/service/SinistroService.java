package com.hermes.hermes.framework.sinistro.service;


import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.sinistro.domain.strategy.SinistroStrategy;
import com.hermes.hermes.framework.exception.BusinessException;
import com.hermes.hermes.framework.exception.InvalidResourceStateException;
import com.hermes.hermes.framework.sinistro.repository.SinistroRepository;
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

    public SinistroBase criar(String tipo, Map<String, Object> dados) {
        SinistroStrategy strategy = sinistroFactory.getStrategy(tipo);
        if (strategy == null)
            throw new BusinessException("Tipo de sinistro não suportado");

        SinistroBase sinistro = strategy.criarSinistro(dados, geocodingService);

        return sinistroRepository.save(sinistro);
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

    public SinistroBase buscarPorId(Long id, String tipo) {
        log.info("Buscando sinistro com ID: {}", id);
        if (id == null) {
            throw new InvalidResourceStateException("ID do sinistro não fornecido");
        }

        SinistroStrategy strategy = sinistroFactory.getStrategy(tipo);
        if (strategy == null)
            throw new BusinessException("Tipo de sinistro não suportado");

        return strategy.buscarPorId(id);
    }
}
