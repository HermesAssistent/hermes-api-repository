package com.hermes.hermes.instancias.automotivo.strategy;

import com.hermes.hermes.framework.exception.BusinessException;
import com.hermes.hermes.instancias.automotivo.domain.model.SinistroAutomotivo;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.exception.NotFoundException;
import com.hermes.hermes.framework.sinistro.domain.strategy.SinistroStrategy;
import com.hermes.hermes.instancias.automotivo.repository.SinistroAutomotivoRepository;
import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SinistroAutomotivoStrategy implements SinistroStrategy {
    private final SinistroAutomotivoRepository sinistroRepository;

    @Override
    public SinistroAutomotivo criarSinistro(Map<String, Object> dados, GeocodingService geocodingService) {
        SinistroAutomotivo sinistro = new SinistroAutomotivo();
        sinistro.fromMap(dados, geocodingService);
        validar(sinistro);
        return sinistro;
    }

    @Override
    public void validar(SinistroBase sinistro) {
        SinistroAutomotivo sinistroAutomotivo = (SinistroAutomotivo) sinistro;
        if (sinistroAutomotivo.getModeloVeiculo() == null) {
            throw new BusinessException("É necessário informar o modelo do veículo");
        }

        if (sinistroAutomotivo.getPlaca() == null) {
            throw new BusinessException("É necessário informar a placa do veículo");
        }
    }

    @Override
    public SinistroBase buscarPorId(Long id) {
        return sinistroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sinistro não encontrado."));
    }

    @Override
    public String getTipo() {
        return "sinistroAutomotivo";
    }
}
