package com.hermes.hermes.framework.usuario.domain.dtos;

import com.hermes.hermes.framework.usuario.domain.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UsuarioDto {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String celular;

    public static UsuarioDto toDto(Usuario usuario) {
        if (usuario == null) {return null;}

        return UsuarioDto.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .telefone(usuario.getTelefone())
                .celular(usuario.getCelular())
                .build();
    }
}
