package com.hermes.hermes.framework.seguradora.domain.dtos;

import com.hermes.hermes.framework.usuario.domain.dtos.UsuarioDto;
import com.hermes.hermes.framework.seguradora.domain.model.Seguradora;
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
