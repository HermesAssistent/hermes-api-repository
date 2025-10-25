package com.hermes.hermes.controller;

import com.hermes.hermes.controller.dto.SinistroDto;
import com.hermes.hermes.service.SinistroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Slf4j
@RequestMapping(value = "v1/sinistro")
@RequiredArgsConstructor
public class SinistroController {
    private final SinistroService sinistroService;

    @GetMapping
    public List<SinistroDto> listarSinistros() {
        return sinistroService.findAll().stream().map(SinistroDto::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public List<SinistroDto> listarSinistrosDoCliente(@PathVariable String id) {
        return sinistroService.findByClienteId(id).stream().map(SinistroDto::fromEntity).toList();
    }

}
