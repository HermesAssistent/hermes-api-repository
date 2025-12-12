package com.hermes.hermes.framework.sinistro.controller;

import com.hermes.hermes.framework.sinistro.domain.dtos.SinistroBaseDto;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;
import com.hermes.hermes.framework.sinistro.service.SinistroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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
    
    @PostMapping("/criar")
    public ResponseEntity<SinistroBaseDto> criarSinistro(@RequestBody Map<String, Object> dados) {
        log.info("Criando sinistro do tipo: {}", dados.get("tipo"));
        
        String tipo = dados.get("tipo").toString();
        SinistroBase sinistro = sinistroService.criar(tipo, dados);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(sinistro.toDto());
    }

}
