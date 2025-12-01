package com.hermes.hermes.domain.sinistro.strategy;

import com.hermes.hermes.domain.model.sinistro.SinistroAutomotivo;
import com.hermes.hermes.domain.model.sinistro.SinistroBase;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.sinistro.SinistroAutomotivoRepository;
import com.hermes.hermes.service.GeocodingService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SinistroAutomotivoStrategy implements SinistroStrategy{
    private GeocodingService geocodingService;
    private SinistroAutomotivoRepository sinistroRepository;

    @Override
    public SinistroAutomotivo criarSinistro(Map<String, Object> dados) {
        return SinistroAutomotivo.fromMap(dados, geocodingService);
    }

    @Override
    public void validar(SinistroBase sinistro) {

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
