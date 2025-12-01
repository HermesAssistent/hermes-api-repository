package com.hermes.hermes.controller;

import com.hermes.hermes.controller.dto.ReviewRequestDto;
import com.hermes.hermes.controller.dto.orcamento.OrcamentoNovoRequestDto;
import com.hermes.hermes.controller.dto.orcamento.OrcamentoNovoResponseDto;
import com.hermes.hermes.domain.enums.StatusOrcamento;
import com.hermes.hermes.domain.model.orcamento.ItemOrcamento;
import com.hermes.hermes.domain.model.orcamento.Orcamento;
import com.hermes.hermes.service.OrcamentoService;
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
    public ResponseEntity<OrcamentoNovoResponseDto> criarNovoOrcamento(@Valid @RequestBody OrcamentoNovoRequestDto dto) {
        log.info("Criando novo orçamento para sinistro {} e prestador {}", dto.getSinistroId(), dto.getPrestadorId());

        Orcamento orcamento = new Orcamento();
        orcamento.setObservacoes(dto.getObservacoes());

        String tipoSinistro = dto.getTipoSinistro();

        Orcamento salvo = orcamentoService.salvar(orcamento, dto.getSinistroId(), dto.getPrestadorId(), tipoSinistro);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToNovoResponseDto(salvo));
    }

    @GetMapping("/novo/sinistro/{sinistroId}")
    public ResponseEntity<List<OrcamentoNovoResponseDto>> listarNovosPorSinistro(@PathVariable Long sinistroId) {
        List<OrcamentoNovoResponseDto> list = orcamentoService.listarPorSinistro(sinistroId).stream()
                .map(this::mapToNovoResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/aceitar")
    public ResponseEntity<OrcamentoNovoResponseDto> aceitarOrcamento(@PathVariable Long id) {
        Orcamento atualizado = orcamentoService.aceitar(id);
        return ResponseEntity.ok(mapToNovoResponseDto(atualizado));
    }

    @PutMapping("/{id}/revisar")
    public ResponseEntity<OrcamentoNovoResponseDto> revisarOrcamento(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDto dto) {
        Orcamento atualizado = orcamentoService.revisar(id, dto.getReviewNotes());
        return ResponseEntity.ok(mapToNovoResponseDto(atualizado));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrcamentoNovoResponseDto>> listarPorStatus(@PathVariable String status) {
        StatusOrcamento statusOrcamento = StatusOrcamento.valueOf(status.toUpperCase());
        List<OrcamentoNovoResponseDto> list = orcamentoService.listarPorStatus(statusOrcamento).stream()
                .map(this::mapToNovoResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    private OrcamentoNovoResponseDto mapToNovoResponseDto(Orcamento orcamento) {
        List<OrcamentoNovoResponseDto.ItemOrcamentoResponseDto> itensDto = null;
        if (orcamento.getItens() != null) {
            itensDto = orcamento.getItens().stream()
                    .map(this::mapItemToDto)
                    .toList();
        }

        return OrcamentoNovoResponseDto.builder()
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

    private OrcamentoNovoResponseDto.ItemOrcamentoResponseDto mapItemToDto(ItemOrcamento item) {
        String tipo = item.getClass().getSimpleName().toUpperCase();
        
        return OrcamentoNovoResponseDto.ItemOrcamentoResponseDto.builder()
                .id(item.getId())
                .descricao(item.getDescricao())
                .valor(item.getValor())
                .quantidade(item.getQuantidade())
                .subtotal(item.calcularSubtotal())
                .tipo(tipo)
                .build();
    }

}