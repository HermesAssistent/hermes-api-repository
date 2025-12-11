package com.hermes.hermes.framework.relatorio.domain.strategy;


import com.hermes.hermes.framework.sinistro.domain.enums.TipoSinistro;
import com.hermes.hermes.framework.sinistro.domain.model.SinistroBase;

/**
 * Strategy para geração de relatórios de diferentes tipos de sinistros
 */
public interface GeradorRelatorioStrategy {

    /**
     * Verifica se esta estratégia suporta o tipo de sinistro informado
     */
    boolean suporta(TipoSinistro tipoSinistro);

    /**
     * Gera o HTML preenchido com os dados do sinistro
     */
    String gerarHtml(SinistroBase sinistro);

    /**
     * Retorna o nome do template HTML a ser utilizado
     */
    String getNomeTemplate();
}