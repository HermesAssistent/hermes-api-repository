package com.hermes.hermes.controller;

import com.hermes.hermes.controller.dto.SinistroDto;
import com.hermes.hermes.domain.model.sinistro.Sinistro;
import com.hermes.hermes.service.SinistroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@Slf4j
@RequestMapping(value = "v1/sinistro")
@RequiredArgsConstructor
public class SinistroController {
    private final SinistroService sinistroService;

    @GetMapping
    public List<SinistroDto> listarSinistros() {
        try {
            return sinistroService.findAll().stream().map(SinistroDto::fromEntity).toList();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao listar sinistros");
        }
    }

    @GetMapping("/{id}")
    public List<SinistroDto> listarSinistrosDoUsuario(@PathVariable String id) {
        try {
            return sinistroService.findByClienteId(id).stream().map(SinistroDto::fromEntity).toList();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao listar sinistro do usuario de id: " + id);
        }
    }

}
