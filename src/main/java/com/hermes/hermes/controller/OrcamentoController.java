package com.hermes.hermes.controller;

import com.hermes.hermes.controller.dto.OrcamentoRequestDto;
import com.hermes.hermes.controller.dto.OrcamentoResponseDto;
import com.hermes.hermes.controller.dto.PecaRequestDto;
import com.hermes.hermes.controller.dto.PecaResponseDto;
import com.hermes.hermes.controller.dto.ReviewRequestDto;
import com.hermes.hermes.domain.model.oficina.Orcamento;
import com.hermes.hermes.domain.model.oficina.Peca;
import com.hermes.hermes.service.OrcamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/orcamentos")
@RequiredArgsConstructor
@Slf4j
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @PostMapping
    public ResponseEntity<OrcamentoResponseDto> criarOrcamento(@Valid @RequestBody OrcamentoRequestDto dto) {
        log.info("Criando orçamento para sinistro {} e oficina {}", dto.getSinistroId(), dto.getOficinaId());

        Orcamento orcamento = mapDtoToEntity(dto);
        Orcamento salvo = orcamentoService.salvar(orcamento, dto.getSinistroId(), dto.getOficinaId());

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(salvo));
    }

    @GetMapping("/sinistro/{sinistroId}")
    public ResponseEntity<List<OrcamentoResponseDto>> listarPorSinistro(@PathVariable Long sinistroId) {
        List<OrcamentoResponseDto> list = orcamentoService.listarPorSinistro(sinistroId).stream()
                .map(this::mapToResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<OrcamentoResponseDto>> listarTodos() {
        List<OrcamentoResponseDto> list = orcamentoService.listarTodos().stream()
                .map(this::mapToResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<OrcamentoResponseDto>> listarPorCliente(@PathVariable Long clienteId) {
        List<OrcamentoResponseDto> list = orcamentoService.listarPorCliente(clienteId).stream()
                .map(this::mapToResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/aceitar")
    public ResponseEntity<OrcamentoResponseDto> aceitarOrcamento(@PathVariable Long id) {
        Orcamento atualizado = orcamentoService.aceitar(id);
        return ResponseEntity.ok(mapToResponseDto(atualizado));
    }

    @PostMapping("/{id}/revisar")
    public ResponseEntity<OrcamentoResponseDto> revisarOrcamento(@PathVariable Long id,
                                                                 @Valid @RequestBody ReviewRequestDto dto) {
        Orcamento atualizado = orcamentoService.revisar(id, dto.getReviewNotes());
        return ResponseEntity.ok(mapToResponseDto(atualizado));
    }

    @GetMapping("/oficina/{oficinaId}")
    public ResponseEntity<List<OrcamentoResponseDto>> listarPorOficina(@PathVariable Long oficinaId) {
        List<OrcamentoResponseDto> list = orcamentoService.listarPorOficina(oficinaId).stream()
                .map(this::mapToResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrcamentoResponseDto> atualizar(@PathVariable Long id,
                                               @Valid @RequestBody OrcamentoRequestDto dto) {
        Orcamento dados = mapDtoToEntity(dto);
        Orcamento atualizado = orcamentoService.atualizar(id, dados, dto.getSinistroId(), dto.getOficinaId());
        return ResponseEntity.ok(mapToResponseDto(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        orcamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private Orcamento mapDtoToEntity(OrcamentoRequestDto dto) {
        Orcamento o = new Orcamento();
        o.setDescricao(dto.getDescricao());
        o.setValorMaoDeObra(dto.getValorMaoDeObra());
        o.setPrazo(dto.getPrazo());

        if (dto.getPecas() != null) {
            List<Peca> pecas = dto.getPecas().stream().map(this::mapPecaDtoToEntity).collect(Collectors.toList());
            o.setPecas(pecas);
        }

        return o;
    }

    private Peca mapPecaDtoToEntity(PecaRequestDto dto) {
        Peca p = new Peca();
        p.setId(dto.getId());
        p.setNome(dto.getNome());
        p.setValor(dto.getValor());
        return p;
    }

    private OrcamentoResponseDto mapToResponseDto(Orcamento o) {
        OrcamentoResponseDto.OrcamentoResponseDtoBuilder b = OrcamentoResponseDto.builder()
                .id(o.getId())
                .descricao(o.getDescricao())
                .valorPecas(o.getValorPecas())
                .valorMaoDeObra(o.getValorMaoDeObra())
                .prazo(o.getPrazo());

        if (o.getSinistro() != null) b.sinistroId(o.getSinistro().getId());
        if (o.getOficina() != null) b.oficinaId(o.getOficina().getId());

        if (o.getPecas() != null) {
            List<PecaResponseDto> pecas = o.getPecas().stream().map(p -> PecaResponseDto.builder()
                    .id(p.getId())
                    .nome(p.getNome())
                    .valor(p.getValor())
                    .build()).toList();
            b.pecas(pecas);
        }

        if (o.getStatus() != null) b.status(o.getStatus().name());
        b.reviewNotes(o.getReviewNotes());

        return b.build();
    }
}