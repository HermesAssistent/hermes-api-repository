package com.hermes.hermes.service.sinistro;


import com.hermes.hermes.domain.model.sinistro.SinistroBase;
import com.hermes.hermes.domain.sinistro.strategy.SinistroStrategy;
import com.hermes.hermes.exception.BusinessException;
import com.hermes.hermes.exception.InvalidResourceStateException;
import com.hermes.hermes.repository.sinistro.SinistroRepository;
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

    public SinistroBase criar(String tipo, Map<String, Object> dados) {
        SinistroStrategy strategy = sinistroFactory.getStrategy(tipo);
        if (strategy == null)
            throw new BusinessException("Tipo de sinistro não suportado");

        SinistroBase sinistro = strategy.criarSinistro(dados);
        strategy.validar(sinistro);

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
