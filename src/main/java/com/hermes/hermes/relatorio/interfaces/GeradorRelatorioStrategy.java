package com.hermes.hermes.relatorio.interfaces;

import com.hermes.hermes.domain.model.sinistro.SinistroBase;

/**
 * Strategy para geração de relatórios de diferentes tipos de sinistros
 */
public interface GeradorRelatorioStrategy {

    /**
     * Verifica se esta estratégia suporta o tipo de sinistro informado
     */
    boolean suporta(String tipoSinistro);

    /**
     * Gera o HTML preenchido com os dados do sinistro
     */
    String gerarHtml(SinistroBase sinistro);

    /**
     * Retorna o nome do template HTML a ser utilizado
     */
    String getNomeTemplate();
}