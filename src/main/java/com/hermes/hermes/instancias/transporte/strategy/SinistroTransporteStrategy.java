package com.hermes.hermes.instancias.transporte.strategy;

import com.hermes.hermes.framework.exception.BusinessException;
import com.hermes.hermes.instancias.transporte.domain.model.SinistroCarga;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.exception.NotFoundException;
import com.hermes.hermes.framework.sinistro.domain.strategy.SinistroStrategy;
import com.hermes.hermes.instancias.transporte.repository.SinistroCargaRepository;
import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SinistroTransporteStrategy implements SinistroStrategy {
    private final SinistroCargaRepository sinistroRepository;

    @Override
    public SinistroCarga criarSinistro(Map<String, Object> dados, GeocodingService geocodingService) {
        SinistroCarga sinistro = SinistroCarga.fromMap(dados, geocodingService);
        validar(sinistro);
        return sinistro;
    }

    @Override
    public void validar(SinistroBase sinistro) {
        SinistroCarga sinistroCarga = (SinistroCarga) sinistro;
        if (sinistroCarga.getDescricaoCarga() == null || sinistroCarga.getDescricaoCarga().isEmpty()) {
            throw new BusinessException("É necessário informar a descrição da carga");
        }
    }

    @Override
    public SinistroBase buscarPorId(Long id) {
        return sinistroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sinistro não encontrado."));
    }

    @Override
    public String getTipo() {
        return "sinistroTransporte";
    }
}
