package com.hermes.hermes.framework.orcamento.controller;

import com.hermes.hermes.framework.orcamento.domain.dtos.ReviewRequestDto;
import com.hermes.hermes.framework.orcamento.domain.dtos.OrcamentoRequestDto;
import com.hermes.hermes.framework.orcamento.domain.dtos.OrcamentoResponseDto;
import com.hermes.hermes.framework.orcamento.domain.enums.StatusOrcamento;
import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.framework.orcamento.domain.model.Orcamento;
import com.hermes.hermes.framework.orcamento.service.OrcamentoService;
import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/orcamentos")
@RequiredArgsConstructor
@Slf4j
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    // ========== NOVOS ENDPOINTS (Nova Estrutura) ==========

    @PostMapping("/novo")
    public ResponseEntity<OrcamentoResponseDto> criarNovoOrcamento(@Valid @RequestBody OrcamentoRequestDto dto) {
        log.info("Criando novo orçamento para sinistro {} e prestador {}", dto.getSinistroId(), dto.getPrestadorId());

        Orcamento orcamento = new Orcamento();
        orcamento.setObservacoes(dto.getObservacoes());

        TipoSinistro tipoSinistro = dto.getTipoSinistro();

        Orcamento salvo = orcamentoService.salvar(orcamento, dto.getSinistroId(), dto.getPrestadorId(), tipoSinistro);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToNovoResponseDto(salvo));
    }

    @GetMapping("/novo/sinistro/{sinistroId}")
    public ResponseEntity<List<OrcamentoResponseDto>> listarNovosPorSinistro(@PathVariable Long sinistroId) {
        List<OrcamentoResponseDto> list = orcamentoService.listarPorSinistro(sinistroId).stream()
                .map(this::mapToNovoResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/aceitar")
    public ResponseEntity<OrcamentoResponseDto> aceitarOrcamento(@PathVariable Long id) {
        Orcamento atualizado = orcamentoService.aceitar(id);
        return ResponseEntity.ok(mapToNovoResponseDto(atualizado));
    }

    @PutMapping("/{id}/revisar")
    public ResponseEntity<OrcamentoResponseDto> revisarOrcamento(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDto dto) {
        Orcamento atualizado = orcamentoService.revisar(id, dto.getReviewNotes());
        return ResponseEntity.ok(mapToNovoResponseDto(atualizado));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrcamentoResponseDto>> listarPorStatus(@PathVariable String status) {
        StatusOrcamento statusOrcamento = StatusOrcamento.valueOf(status.toUpperCase());
        List<OrcamentoResponseDto> list = orcamentoService.listarPorStatus(statusOrcamento).stream()
                .map(this::mapToNovoResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    private OrcamentoResponseDto mapToNovoResponseDto(Orcamento orcamento) {
        List<OrcamentoResponseDto.ItemOrcamentoResponseDto> itensDto = null;
        if (orcamento.getItens() != null) {
            itensDto = orcamento.getItens().stream()
                    .map(this::mapItemToDto)
                    .toList();
        }

        return OrcamentoResponseDto.builder()
                .id(orcamento.getId())
                .valorTotal(orcamento.getValorTotal())
                .status(orcamento.getStatus())
                .sinistroId(orcamento.getSinistro() != null ? orcamento.getSinistro().getId() : null)
                .prestadorId(orcamento.getPrestador() != null ? orcamento.getPrestador().getId() : null)
                .observacoes(orcamento.getObservacoes())
                .dataCriacao(orcamento.getDataCriacao())
                .dataAtualizacao(orcamento.getDataAtualizacao())
                .itens(itensDto)
                .build();
    }

    private OrcamentoResponseDto.ItemOrcamentoResponseDto mapItemToDto(ItemOrcamento item) {
        String tipo = item.getClass().getSimpleName().toUpperCase();
        
        return OrcamentoResponseDto.ItemOrcamentoResponseDto.builder()
                .id(item.getId())
                .descricao(item.getDescricao())
                .valor(item.getValor())
                .quantidade(item.getQuantidade())
                .subtotal(item.calcularSubtotal())
                .tipo(tipo)
                .build();
    }

}