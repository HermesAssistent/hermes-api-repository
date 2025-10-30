package com.hermes.hermes.controller;

import com.hermes.hermes.controller.dto.OficinaRequestDto;
import com.hermes.hermes.controller.dto.OficinaResponseDto;
import com.hermes.hermes.controller.mapper.OficinaMapper;
import com.hermes.hermes.domain.model.oficina.Oficina;
import com.hermes.hermes.service.OficinaService;
import com.hermes.hermes.service.GeocodingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/oficinas")
public class OficinaController {

    private final OficinaService oficinaService;
    private final OficinaMapper oficinaMapper;
    private final GeocodingService geocodingService;

    @GetMapping("/proximas/{sinistroId}")
    public ResponseEntity<List<OficinaResponseDto>> getOficinasProximas(
            @PathVariable Long sinistroId,
            @RequestParam("origem") OficinaService.OrigemBusca origem,
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(value = "lon", required = false) Double lon,
            @RequestParam(value = "endereco", required = false) String endereco) {

        log.info("Buscando oficinas próximas ao sinistro: {}, origem: {}",
                sinistroId, origem);

        List<OficinaResponseDto> oficinas = oficinaService
                .findOficinasProximas(sinistroId, origem, lat, lon, endereco)
                .stream()
                .map(oficinaMapper::toResponseDto)
                .toList();

        return ResponseEntity.ok(oficinas);
    }

    @GetMapping
    public ResponseEntity<List<OficinaResponseDto>> getOficinas() {
        log.info("Listando todas as oficinas");

        List<OficinaResponseDto> oficinas = oficinaService
                .findAll()
                .stream()
                .map(oficinaMapper::toResponseDto)
                .toList();

        return ResponseEntity.ok(oficinas);
    }

    @GetMapping("/seguradora/{seguradoraId}")
    public ResponseEntity<List<OficinaResponseDto>> getOficinasPorSeguradora(@PathVariable Long seguradoraId) {
        log.info("Listando oficinas para seguradora: {}", seguradoraId);

        List<OficinaResponseDto> oficinas = oficinaService.findBySeguradora(seguradoraId).stream()
                .map(oficinaMapper::toResponseDto)
                .toList();

        return ResponseEntity.ok(oficinas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OficinaResponseDto> getOficinaPorId(@PathVariable Long id) {
        log.info("Buscando oficina: {}", id);

        Oficina oficina = oficinaService.findById(id);
        return ResponseEntity.ok(oficinaMapper.toResponseDto(oficina));
    }

    @PostMapping
    public ResponseEntity<OficinaResponseDto> criarOficina(
            @Valid @RequestBody OficinaRequestDto dto) {
        log.info("Criando nova oficina: {}", dto.getNome());

        Oficina oficina = oficinaMapper.toEntity(dto);
        // Se o CEP foi informado, usar o GeocodingService para obter endereço completo e coordenadas
        try {
            if (dto.getCep() != null && !dto.getCep().isEmpty()) {
                var localizacao = geocodingService.getCoordinates(dto.getCep());
                if (localizacao != null) {
                    oficina.setLocalizacao(localizacao);
                }
            }
        } catch (Exception e) {
            log.warn("Não foi possível obter endereço/coordenadas pelo CEP {}: {}", dto.getCep(), e.getMessage());
            // Continua sem lançar erro — a service ainda tentará geocodificar caso necessário
        }
        Oficina novaOficina = oficinaService.create(oficina);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(oficinaMapper.toResponseDto(novaOficina));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OficinaResponseDto> atualizarOficina(
            @PathVariable Long id,
            @Valid @RequestBody OficinaRequestDto dto) {
        log.info("Atualizando oficina: {}", id);

        Oficina oficina = oficinaMapper.toEntity(dto);
        Oficina atualizada = oficinaService.update(id, oficina);

        return ResponseEntity.ok(oficinaMapper.toResponseDto(atualizada));
    }

    @PostMapping("/{id}/seguradoras/{seguradoraId}")
    public ResponseEntity<OficinaResponseDto> adicionarSeguradora(
            @PathVariable Long id,
            @PathVariable Long seguradoraId) {
        log.info("Adicionando seguradora {} à oficina {}", seguradoraId, id);

        Oficina atualizada = oficinaService.addSeguradora(id, seguradoraId);
        return ResponseEntity.ok(oficinaMapper.toResponseDto(atualizada));
    }

    @DeleteMapping("/{id}/seguradoras/{seguradoraId}")
    public ResponseEntity<OficinaResponseDto> removerSeguradora(
            @PathVariable Long id,
            @PathVariable Long seguradoraId) {
        log.info("Removendo seguradora {} da oficina {}", seguradoraId, id);

        Oficina atualizada = oficinaService.removeSeguradora(id, seguradoraId);
        return ResponseEntity.ok(oficinaMapper.toResponseDto(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOficina(@PathVariable Long id) {
        log.info("Deletando oficina: {}", id);

        oficinaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
