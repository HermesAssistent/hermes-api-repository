package com.hermes.hermes.framework.seguradora.controller;
import com.hermes.hermes.framework.cliente.domain.dtos.ClienteResponseDto;
import com.hermes.hermes.framework.seguradora.domain.dtos.SeguradoraResponseListagemDto;
import com.hermes.hermes.framework.seguradora.service.SeguradoraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/v1/seguradora")
@RequiredArgsConstructor
public class SeguradoraController {
    private final SeguradoraService seguradoraService;

    @GetMapping
    public ResponseEntity<List<SeguradoraResponseListagemDto>> getSeguradoras() {
       return ResponseEntity.ok(seguradoraService.findAll().stream().map(SeguradoraResponseListagemDto::toDto).collect(Collectors.toList()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<SeguradoraResponseListagemDto> getSeguradora(@PathVariable String id) {
        return ResponseEntity.ok(SeguradoraResponseListagemDto.toDto(seguradoraService.findById(Long.parseLong(id))));
    }

    @GetMapping("/listar-para-clientes")
    public ResponseEntity<List<SeguradoraResponseListagemDto>> getSeguradorasParaClientes() {
        return ResponseEntity.ok(seguradoraService.findAll().stream().map(SeguradoraResponseListagemDto::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/listar-clientes/{seguradoraId}")
    public ResponseEntity<List<ClienteResponseDto.ClienteDto>> getClientesDaSeguradora(@PathVariable String seguradoraId) {
        return ResponseEntity.ok(seguradoraService.findByClientesDaSeguradora(Long.parseLong(seguradoraId)).stream().map(ClienteResponseDto.ClienteDto::from).collect(Collectors.toList()));
    }
}
