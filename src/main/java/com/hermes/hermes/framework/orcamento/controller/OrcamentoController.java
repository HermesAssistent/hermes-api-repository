package com.hermes.hermes.framework.orcamento.controller;

import com.hermes.hermes.framework.orcamento.domain.dtos.ReviewRequestDto;
import com.hermes.hermes.framework.orcamento.domain.dtos.OrcamentoRequestDto;
import com.hermes.hermes.framework.orcamento.domain.dtos.OrcamentoResponseDto;
import com.hermes.hermes.framework.orcamento.domain.enums.StatusOrcamento;
import com.hermes.hermes.framework.orcamento.domain.model.ItemOrcamento;
import com.hermes.hermes.framework.orcamento.domain.model.Orcamento;
import com.hermes.hermes.framework.orcamento.service.OrcamentoService;
import com.hermes.hermes.instancias.automotivo.domain.model.MaoDeObra;
import com.hermes.hermes.instancias.automotivo.domain.model.Peca;
import com.hermes.hermes.instancias.domestico.domain.model.Material;
import com.hermes.hermes.instancias.domestico.domain.model.ServicoTecnico;
import com.hermes.hermes.instancias.transporte.domain.model.CustoLogistico;
import com.hermes.hermes.instancias.transporte.domain.model.CustoPericial;
import com.hermes.hermes.instancias.transporte.domain.model.CustoReposicao;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/orcamentos")
@RequiredArgsConstructor
@Slf4j
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @PostMapping("/novo")
    public ResponseEntity<OrcamentoResponseDto> criarNovoOrcamento(@Valid @RequestBody OrcamentoRequestDto dto) {
        log.info("Criando novo orçamento para sinistro {} e prestador {}", dto.getSinistroId(), dto.getPrestadorId());

        Orcamento orcamento = new Orcamento();
        orcamento.setObservacoes(dto.getObservacoes());
        
        // Converte itens do DTO para entidades específicas
        if (dto.getItens() != null && !dto.getItens().isEmpty()) {
            List<ItemOrcamento> itens = dto.getItens().stream()
                    .map(this::mapDtoToEntity)
                    .toList();
            orcamento.setItens(new ArrayList<>(itens));
        }

        String tipoSinistro = dto.getTipoSinistro();

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
    
    @PostMapping("/{orcamentoId}/itens")
    public ResponseEntity<OrcamentoResponseDto> adicionarItem(
            @PathVariable Long orcamentoId,
            @Valid @RequestBody OrcamentoRequestDto.ItemOrcamentoRequestDto itemDto) {
        log.info("Adicionando item ao orçamento {}", orcamentoId);
        
        ItemOrcamento item = mapDtoToEntity(itemDto);
        Orcamento atualizado = orcamentoService.adicionarItem(orcamentoId, item);
        
        return ResponseEntity.ok(mapToNovoResponseDto(atualizado));
    }
    
    @PostMapping("/{orcamentoId}/itens/lote")
    public ResponseEntity<OrcamentoResponseDto> adicionarItensEmLote(
            @PathVariable Long orcamentoId,
            @Valid @RequestBody List<OrcamentoRequestDto.ItemOrcamentoRequestDto> itensDto) {
        log.info("Adicionando {} itens ao orçamento {}", itensDto.size(), orcamentoId);
        
        List<ItemOrcamento> itens = itensDto.stream()
                .map(this::mapDtoToEntity)
                .toList();
        
        Orcamento atualizado = orcamentoService.adicionarItensEmLote(orcamentoId, itens);
        
        return ResponseEntity.ok(mapToNovoResponseDto(atualizado));
    }
    
    @DeleteMapping("/{orcamentoId}/itens/{itemId}")
    public ResponseEntity<OrcamentoResponseDto> removerItem(
            @PathVariable Long orcamentoId,
            @PathVariable Long itemId) {
        log.info("Removendo item {} do orçamento {}", itemId, orcamentoId);
        
        Orcamento atualizado = orcamentoService.removerItem(orcamentoId, itemId);
        
        return ResponseEntity.ok(mapToNovoResponseDto(atualizado));
    }
    
    @PutMapping("/{orcamentoId}/itens/{itemId}")
    public ResponseEntity<OrcamentoResponseDto> atualizarItem(
            @PathVariable Long orcamentoId,
            @PathVariable Long itemId,
            @Valid @RequestBody OrcamentoRequestDto.ItemOrcamentoRequestDto itemDto) {
        log.info("Atualizando item {} do orçamento {}", itemId, orcamentoId);
        
        ItemOrcamento itemAtualizado = mapDtoToEntity(itemDto);
        Orcamento atualizado = orcamentoService.atualizarItem(orcamentoId, itemId, itemAtualizado);
        
        return ResponseEntity.ok(mapToNovoResponseDto(atualizado));
    }
    
    @GetMapping("/{orcamentoId}")
    public ResponseEntity<OrcamentoResponseDto> buscarPorId(@PathVariable Long orcamentoId) {
        Orcamento orcamento = orcamentoService.buscarPorId(orcamentoId);
        return ResponseEntity.ok(mapToNovoResponseDto(orcamento));
    }

    @GetMapping
    public ResponseEntity<List<OrcamentoResponseDto>> listarTodos() {
        List<OrcamentoResponseDto> list = orcamentoService.listarTodos().stream()
                .map(this::mapToNovoResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<OrcamentoResponseDto>> listarPorCliente(@PathVariable Long clienteId) {
        List<OrcamentoResponseDto> list = orcamentoService.listarPorCliente(clienteId).stream()
                .map(this::mapToNovoResponseDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrcamentoResponseDto> atualizarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        StatusOrcamento novoStatus = StatusOrcamento.valueOf(status.toUpperCase());
        Orcamento atualizado = orcamentoService.atualizarStatus(id, novoStatus);
        return ResponseEntity.ok(mapToNovoResponseDto(atualizado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrcamentoResponseDto> atualizarOrcamento(
            @PathVariable Long id,
            @Valid @RequestBody OrcamentoRequestDto dto) {
        log.info("Atualizando orçamento {}", id);
        
        Orcamento orcamentoExistente = orcamentoService.buscarPorId(id);
        
        // Atualiza observações se fornecido
        if (dto.getObservacoes() != null) {
            orcamentoExistente.setObservacoes(dto.getObservacoes());
        }
        
        // Atualiza prestador se fornecido
        if (dto.getPrestadorId() != null) {
            Orcamento atualizado = orcamentoService.atualizarPrestador(id, dto.getPrestadorId());
            return ResponseEntity.ok(mapToNovoResponseDto(atualizado));
        }
        
        Orcamento salvo = orcamentoService.salvar(orcamentoExistente, dto.getSinistroId(), dto.getPrestadorId(), dto.getTipoSinistro());
        return ResponseEntity.ok(mapToNovoResponseDto(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOrcamento(@PathVariable Long id) {
        log.info("Deletando orçamento {}", id);
        orcamentoService.deletar(id);
        return ResponseEntity.noContent().build();
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
    
    private ItemOrcamento mapDtoToEntity(OrcamentoRequestDto.ItemOrcamentoRequestDto dto) {
        String tipo = dto.getTipo().toUpperCase();
        
        return switch (tipo) {
            case "PECA" -> criarPeca(dto);
            case "MAO_DE_OBRA" -> criarMaoDeObra(dto);
            case "MATERIAL" -> criarMaterial(dto);
            case "SERVICO_TECNICO" -> criarServicoTecnico(dto);
            case "CUSTO_PERICIAL" -> criarCustoPericial(dto);
            case "CUSTO_REPOSICAO" -> criarCustoReposicao(dto);
            case "CUSTO_LOGISTICO" -> criarCustoLogistico(dto);
            default -> throw new IllegalArgumentException("Tipo de item desconhecido: " + tipo);
        };
    }
    
    private Peca criarPeca(OrcamentoRequestDto.ItemOrcamentoRequestDto dto) {
        Peca peca = new Peca();
        peca.setCodigo(dto.getCodigo());
        peca.setDescricao(dto.getDescricao());
        peca.setCategoria(dto.getCategoria());
        peca.setMarca(dto.getMarca());
        peca.setValor(dto.getValor());
        peca.setQuantidade(dto.getQuantidade());
        return peca;
    }
    
    private MaoDeObra criarMaoDeObra(OrcamentoRequestDto.ItemOrcamentoRequestDto dto) {
        MaoDeObra maoDeObra = new MaoDeObra();
        maoDeObra.setDescricao(dto.getDescricao());
        maoDeObra.setEspecialidade(dto.getEspecialidade());
        maoDeObra.setHorasEstimadas(dto.getHorasEstimadas());
        maoDeObra.setValorHora(dto.getValorHora());
        maoDeObra.setValor(dto.getValor());
        maoDeObra.setQuantidade(dto.getQuantidade());
        return maoDeObra;
    }
    
    private Material criarMaterial(OrcamentoRequestDto.ItemOrcamentoRequestDto dto) {
        Material material = new Material();
        material.setDescricao(dto.getDescricao());
        material.setUnidadeMedida(dto.getUnidadeMedida());
        material.setCategoria(dto.getCategoria());
        material.setFornecedor(dto.getFornecedor());
        material.setValor(dto.getValor());
        material.setQuantidade(dto.getQuantidade());
        return material;
    }
    
    private ServicoTecnico criarServicoTecnico(OrcamentoRequestDto.ItemOrcamentoRequestDto dto) {
        ServicoTecnico servico = new ServicoTecnico();
        servico.setDescricao(dto.getDescricao());
        servico.setEspecialidade(dto.getEspecialidade());
        servico.setHorasEstimadas(dto.getHorasEstimadas());
        servico.setValorHora(dto.getValorHora());
        servico.setValor(dto.getValor());
        servico.setQuantidade(dto.getQuantidade());
        return servico;
    }
    
    private CustoPericial criarCustoPericial(OrcamentoRequestDto.ItemOrcamentoRequestDto dto) {
        CustoPericial custo = new CustoPericial();
        custo.setDescricao(dto.getDescricao());
        custo.setValor(dto.getValor());
        custo.setQuantidade(dto.getQuantidade());
        
        // Mapeia TipoAvaliacao se fornecido
        if (dto.getTipoAvaliacao() != null) {
            custo.setTipoAvaliacao(
                com.hermes.hermes.instancias.transporte.domain.enums.TipoAvaliacao.valueOf(
                    dto.getTipoAvaliacao().toUpperCase()
                )
            );
        }
        
        return custo;
    }
    
    private CustoReposicao criarCustoReposicao(OrcamentoRequestDto.ItemOrcamentoRequestDto dto) {
        CustoReposicao custo = new CustoReposicao();
        custo.setDescricao(dto.getDescricao());
        custo.setValorCarga(dto.getValorCarga());
        custo.setPercentualPerda(dto.getPercentualPerda());
        custo.setValor(dto.getValor());
        custo.setQuantidade(dto.getQuantidade());
        return custo;
    }
    
    private CustoLogistico criarCustoLogistico(OrcamentoRequestDto.ItemOrcamentoRequestDto dto) {
        CustoLogistico custo = new CustoLogistico();
        custo.setDescricao(dto.getDescricao());
        custo.setUnidadeMedida(dto.getUnidadeMedida());
        custo.setValor(dto.getValor());
        custo.setQuantidade(dto.getQuantidade());
        
        // Mapeia TipoCustoLogistico se fornecido
        if (dto.getTipoCustoLogistico() != null) {
            custo.setTipo(
                com.hermes.hermes.instancias.transporte.domain.enums.TipoCustoLogistico.valueOf(
                    dto.getTipoCustoLogistico().toUpperCase()
                )
            );
        }
        
        return custo;
    }

    @GetMapping("/formulario/{tipoSinistro}")
    public ResponseEntity<java.util.Map<String, Object>> obterFormularioOrcamento(@PathVariable String tipoSinistro) {
        log.info("Buscando formulário de orçamento para tipo: {}", tipoSinistro);
        
        java.util.Map<String, Object> formulario = orcamentoService.obterFormularioPorTipo(tipoSinistro);
        
        return ResponseEntity.ok(formulario);
    }

}