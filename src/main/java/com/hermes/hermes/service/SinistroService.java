package com.hermes.hermes.service;


import com.hermes.hermes.domain.model.sinistro.Sinistro;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.SinistroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SinistroService {
    private final SinistroRepository sinistroRepository;

    public List<Sinistro> findAll() {
        return sinistroRepository.findAll();
    }

    public List<Sinistro> findByClienteId(String id) {
        return sinistroRepository.findAllByCliente_IdIs(Long.parseLong(id));
    }

    public Sinistro buscarPorId(Long id) {
        return sinistroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sinistro não encontrado."));
    }
}
