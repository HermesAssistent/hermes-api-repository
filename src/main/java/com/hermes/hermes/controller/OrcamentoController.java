package com.hermes.hermes.controller;

import com.hermes.hermes.controller.dto.OrcamentoRequestDto;
import com.hermes.hermes.controller.dto.PecaRequestDto;
import com.hermes.hermes.domain.model.oficina.Orcamento;
import com.hermes.hermes.domain.model.oficina.Peca;
import com.hermes.hermes.service.OrcamentoService;
import com.hermes.hermes.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    @Autowired
    private OrcamentoService orcamentoService;

    @PostMapping
    public ResponseEntity<Orcamento> criarOrcamento(@Valid @RequestBody OrcamentoRequestDto dto) {
        Orcamento o = new Orcamento();
        o.setDescricao(dto.getDescricao());
        o.setValorMaoDeObra(dto.getValorMaoDeObra());
        o.setPrazo(dto.getPrazo());

        if (dto.getPecas() != null) {
            List<Peca> pecas = dto.getPecas().stream().map(pdto -> {
                Peca p = new Peca();
                p.setId(pdto.getId());
                p.setNome(pdto.getNome());
                p.setValor(pdto.getValor());
                return p;
            }).collect(Collectors.toList());
            o.setPecas(pecas);
        }

        Orcamento salvo = orcamentoService.salvar(o, dto.getSinistroId(), dto.getOficinaId());
        return ResponseEntity.status(201).body(salvo);
    }

    @GetMapping("/sinistro/{sinistroId}")
    public ResponseEntity<List<Orcamento>> listarPorSinistro(@PathVariable Long sinistroId) {
        List<Orcamento> lista = orcamentoService.listarPorSinistro(sinistroId);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/oficina/{oficinaId}")
    public ResponseEntity<List<Orcamento>> listarPorOficina(@PathVariable Long oficinaId) {
        List<Orcamento> lista = orcamentoService.listarPorOficina(oficinaId);
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Orcamento> atualizarOrcamento(@PathVariable Long id, @Valid @RequestBody OrcamentoRequestDto dto) {
        Orcamento o = new Orcamento();
        o.setDescricao(dto.getDescricao());
        o.setValorMaoDeObra(dto.getValorMaoDeObra());
        o.setPrazo(dto.getPrazo());

        if (dto.getPecas() != null) {
            List<Peca> pecas = dto.getPecas().stream().map(pdto -> {
                Peca p = new Peca();
                p.setId(pdto.getId());
                p.setNome(pdto.getNome());
                p.setValor(pdto.getValor());
                return p;
            }).collect(Collectors.toList());
            o.setPecas(pecas);
        }

        Orcamento atualizado = orcamentoService.atualizar(id, o, dto.getSinistroId(), dto.getOficinaId());
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOrcamento(@PathVariable Long id) {
        orcamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
