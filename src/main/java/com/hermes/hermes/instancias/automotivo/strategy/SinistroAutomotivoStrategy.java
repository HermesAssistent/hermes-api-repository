package com.hermes.hermes.instancias.automotivo.strategy;

import com.hermes.hermes.framework.exception.BusinessException;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import com.hermes.hermes.instancias.automotivo.domain.model.SinistroAutomotivo;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.exception.NotFoundException;
import com.hermes.hermes.framework.sinistro.domain.strategy.SinistroStrategy;
import com.hermes.hermes.instancias.automotivo.repository.SinistroAutomotivoRepository;
import com.hermes.hermes.framework.localizacao.service.GeocodingService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SinistroAutomotivoStrategy implements SinistroStrategy {

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

        // Validação de placa (formato brasileiro simplificado)
        if (!validarPlaca(sinistroAutomotivo.getPlaca())) {
            throw new BusinessException("Placa do veículo em formato inválido");
        }

        // Se tem seguro, deve informar a seguradora
        if (Boolean.TRUE.equals(sinistroAutomotivo.getPossuiSeguro())) {
            if (sinistroAutomotivo.getSeguradora() == null || sinistroAutomotivo.getSeguradora().isBlank()) {
                throw new BusinessException("É necessário informar a seguradora quando possui seguro");
            }
        }
    }

    private boolean validarPlaca(String placa) {
        if (placa == null) return false;
        String placaLimpa = placa.replaceAll("[\\s-]", "").toUpperCase();
        // Formato antigo: AAA9999 ou Mercosul: AAA9A99
        return placaLimpa.matches("^[A-Z]{3}\\d{4}$") || placaLimpa.matches("^[A-Z]{3}\\d[A-Z]\\d{2}$");
    }

    @Override
    public TipoSinistro getTipo() {
        return TipoSinistro.AUTOMOTIVO;
    }
}
