package com.hermes.hermes.controller;

import com.hermes.hermes.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(value = "v1/cliente")
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteService clienteService;

    //todo: fazer listagem dos clientes

    //todo: fazer cadastro de cliente

    //todo: fazer edicao de cliente
}
