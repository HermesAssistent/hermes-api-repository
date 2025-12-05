package com.hermes.hermes.framework.sinistro.controller;

import com.hermes.hermes.framework.sinistro.domain.dtos.SinistroBaseDto;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.sinistro.service.SinistroService;
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
    public List<SinistroBaseDto> listarSinistros() {
        return sinistroService.findAll().stream().map(SinistroBase::toDto).toList();
    }

    @GetMapping("/{id}")
    public List<SinistroBaseDto> listarSinistrosDoCliente(@PathVariable String id) {
        return sinistroService.findByClienteId(id).stream().map(SinistroBase::toDto).toList();
    }

}
