package com.hermes.hermes.controller.mapper;

import com.hermes.hermes.controller.dto.OficinaRequestDto;
import com.hermes.hermes.controller.dto.OficinaResponseDto;
import com.hermes.hermes.domain.model.localizacao.Localizacao;
import com.hermes.hermes.domain.model.oficina.Oficina;
import com.hermes.hermes.domain.model.seguradora.Seguradora;
import com.hermes.hermes.controller.dto.SeguradoraResponseListagemDto;
import org.springframework.stereotype.Component;

@Component
public class OficinaMapper {

    public Oficina toEntity(OficinaRequestDto dto) {
        Oficina oficina = new Oficina();
        oficina.setNome(dto.getNome());
        oficina.setTelefone(dto.getTelefone());
        oficina.setEspecialidades(dto.getEspecialidades());
        // Localizacao constructor: (endereco, latitude, longitude, cep)
        oficina.setLocalizacao(new Localizacao(dto.getEndereco(), dto.getLatitude(), dto.getLongitude(), dto.getCep()));
        if (dto.getSeguradoraIds() != null) {
            java.util.Set<Seguradora> seguradoras = new java.util.HashSet<>();
            for (Long id : dto.getSeguradoraIds()) {
                Seguradora s = new Seguradora();
                s.setId(id);
                seguradoras.add(s);
            }
            oficina.setSeguradoras(seguradoras);
        }
        return oficina;
    }

    public OficinaResponseDto toResponseDto(Oficina oficina) {
        OficinaResponseDto dto = new OficinaResponseDto();
        dto.setId(oficina.getId());
        dto.setNome(oficina.getNome());
        dto.setTelefone(oficina.getTelefone());
        if (oficina.getLocalizacao() != null) {
            dto.setEndereco(oficina.getLocalizacao().getEndereco());
            dto.setLatitude(oficina.getLocalizacao().getLatitude());
            dto.setLongitude(oficina.getLocalizacao().getLongitude());
            dto.setCep(oficina.getLocalizacao().getCep());
        }
        dto.setEspecialidades(oficina.getEspecialidades());
        if (oficina.getSeguradoras() != null) {
            java.util.List<SeguradoraResponseListagemDto> lista = oficina.getSeguradoras().stream()
                    .map(SeguradoraResponseListagemDto::toDto)
                    .toList();
            dto.setSeguradoras(lista);
        }
        return dto;
    }
}