package com.hermes.hermes.controller.dto;

import com.hermes.hermes.domain.model.seguradora.Seguradora;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SeguradoraResponseListagemDto {
        private Long id;
        private UsuarioDto usuario;
        private String endereco;
        private String cnpj;


    public static SeguradoraResponseListagemDto toDto(Seguradora seguradora) {
       if(seguradora == null) return null;

       return SeguradoraResponseListagemDto.builder()
               .id(seguradora.getId())
               .usuario(UsuarioDto.toDto(seguradora.getUsuario()))
               .endereco(seguradora.getUsuario().getEndereco())
               .cnpj(seguradora.getCnpj())
               .build();
    }


}
