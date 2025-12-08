package com.hermes.hermes.framework.cliente.domain.dtos;

import com.hermes.hermes.framework.usuario.domain.dtos.UsuarioDto;
import com.hermes.hermes.framework.cliente.domain.model.Cliente;
import com.hermes.hermes.framework.seguradora.domain.dtos.SeguradoraResponseListagemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClienteResponseDto {
    private ClienteDto user;
    private String token;


    @Data
    @AllArgsConstructor
    @Builder
    public static class ClienteDto {
        private Long id;
        private String cpf;
        private String veiculo;

        private UsuarioDto usuario;

        private SeguradoraResponseListagemDto seguradora;

        private Double latitude;

        private Double longitude;

        public static ClienteDto from(Cliente cliente) {
            if(cliente == null) return null;

            return ClienteDto.builder()
                    .id(cliente.getId())
                    .cpf(cliente.getCpf())
                    .veiculo(cliente.getVeiculo())
                    .usuario(UsuarioDto.toDto(cliente.getUsuario()))
                    .seguradora(SeguradoraResponseListagemDto.toDto(cliente.getSeguradora()))
                    .latitude(cliente.getLatitude())
                    .longitude(cliente.getLongitude())
                    .build();
        }
    }
}
