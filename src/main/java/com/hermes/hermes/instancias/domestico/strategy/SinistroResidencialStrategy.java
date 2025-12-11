package com.hermes.hermes.instancias.domestico.strategy;

import com.hermes.hermes.framework.exception.BusinessException;
import com.hermes.hermes.instancias.domestico.domain.model.SinistroResidencial;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.exception.NotFoundException;
import com.hermes.hermes.framework.sinistro.domain.strategy.SinistroStrategy;
import com.hermes.hermes.instancias.domestico.repository.SinistroResidencialRepository;
import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SinistroResidencialStrategy implements SinistroStrategy {
    private final SinistroResidencialRepository sinistroRepository;

    @Override
    public SinistroResidencial criarSinistro(Map<String, Object> dados, GeocodingService geocodingService) {
        SinistroResidencial sinistro = SinistroResidencial.fromMap(dados, geocodingService);
        validar(sinistro);
        return sinistro;
    }

    @Override
    public void validar(SinistroBase sinistro) {
        SinistroResidencial sinistroResidencial = (SinistroResidencial) sinistro;
        if (sinistroResidencial.getProblema() == null || sinistroResidencial.getProblema().isEmpty()) {
            throw new BusinessException("É necessário informar o problema");
        }
    }

    @Override
    public SinistroBase buscarPorId(Long id) {
        return sinistroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sinistro não encontrado."));
    }

    @Override
    public String getTipo() {
        return "sinistroResidencial";
    }
}
