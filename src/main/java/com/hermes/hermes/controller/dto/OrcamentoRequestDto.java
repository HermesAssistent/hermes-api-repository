package com.hermes.hermes.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrcamentoRequestDto {

    private String descricao;

    @PositiveOrZero
    private BigDecimal valorMaoDeObra;

    private LocalDate prazo;

    private Long sinistroId;

    private Long oficinaId;

    private List<PecaRequestDto> pecas;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorMaoDeObra() {
        return valorMaoDeObra;
    }

    public void setValorMaoDeObra(BigDecimal valorMaoDeObra) {
        this.valorMaoDeObra = valorMaoDeObra;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public Long getSinistroId() {
        return sinistroId;
    }

    public void setSinistroId(Long sinistroId) {
        this.sinistroId = sinistroId;
    }

    public Long getOficinaId() {
        return oficinaId;
    }

    public void setOficinaId(Long oficinaId) {
        this.oficinaId = oficinaId;
    }

    public List<PecaRequestDto> getPecas() {
        return pecas;
    }

    public void setPecas(List<PecaRequestDto> pecas) {
        this.pecas = pecas;
    }
}
