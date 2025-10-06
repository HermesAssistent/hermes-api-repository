package com.hermes.hermes.controller;

import com.hermes.hermes.domain.model.oficina.Oficina;
import com.hermes.hermes.service.OficinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/oficinas")
public class OficinaController {

    @Autowired
    private OficinaService oficinaService;

    @GetMapping("/proximas/{sinistroId}")
    public ResponseEntity<List<Oficina>> getOficinasProximas(
            @PathVariable Long sinistroId,
            @RequestParam("origem") OficinaService.OrigemBusca origem,
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(value = "lon", required = false) Double lon,
            @RequestParam(value = "endereco", required = false) String endereco) {

        List<Oficina> oficinas = oficinaService.findOficinasProximas(sinistroId, origem, lat, lon, endereco);
        return ResponseEntity.ok(oficinas);
    }

    @GetMapping
    public ResponseEntity<List<Oficina>> getOficinas() {
        List<Oficina> oficinas = oficinaService.findAll();
        return ResponseEntity.ok(oficinas);
    }

    @PostMapping
    public ResponseEntity<Oficina> criarOficina(@RequestBody Oficina oficina) {
        Oficina novaOficina = oficinaService.create(oficina);
        return ResponseEntity.status(201).body(novaOficina);
    }
}
