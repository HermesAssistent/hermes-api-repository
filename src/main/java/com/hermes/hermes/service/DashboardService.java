package com.hermes.hermes.service;

import com.hermes.hermes.controller.dto.dashboard.*;
import com.hermes.hermes.exception.BusinessException;
import com.hermes.hermes.exception.InvalidResourceStateException;
import com.hermes.hermes.repository.ClienteRepository;
import com.hermes.hermes.repository.OficinaRepository;
import com.hermes.hermes.repository.SeguradoraRepository;
import com.hermes.hermes.repository.SinistroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final SinistroRepository sinistroRepository;
    private final OficinaRepository oficinaRepository;
    private final SeguradoraRepository seguradoraRepository;

    public DashboardStatsDTO getStats() {
        log.info("Calculando estatísticas gerais do dashboard");

        try {
            Long totalClientes = clienteRepository.count();
            Long totalSinistros = sinistroRepository.count();
            Long totalOficinas = oficinaRepository.count();
            Long sinistrosComFeridos = sinistroRepository.countByFeridosTrue();
            Long sinistrosAtivos = sinistroRepository.countSinistrosAtivos();

            // Buscar contagem por gravidade
            Long baixa = sinistroRepository.countByGravidadeEqualsIgnoreCase("BAIXA");
            Long moderada = sinistroRepository.countByGravidadeEqualsIgnoreCase("MODERADA");
            Long alta = sinistroRepository.countByGravidadeEqualsIgnoreCase("ALTA");

            // Garantir que valores não sejam null
            baixa = baixa != null ? baixa : 0L;
            moderada = moderada != null ? moderada : 0L;
            alta = alta != null ? alta : 0L;

            GravidadeStatsDTO gravidade = GravidadeStatsDTO.builder()
                    .baixa(baixa)
                    .moderada(moderada)
                    .alta(alta)
                    .build();

            return DashboardStatsDTO.builder()
                    .totalClientes(totalClientes.intValue())
                    .totalSinistros(totalSinistros.intValue())
                    .totalOficinas(totalOficinas.intValue())
                    .sinistrosComFeridos(sinistrosComFeridos != null ? sinistrosComFeridos.intValue() : 0)
                    .sinistrosAtivos(sinistrosAtivos != null ? sinistrosAtivos.intValue() : 0)
                    .gravidade(gravidade)
                    .build();
        } catch (Exception e) {
            log.error("Erro ao calcular estatísticas do dashboard: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao calcular estatísticas do dashboard: " + e.getMessage());
        }
    }

    public List<SinistroMensalDTO> getSinistrosMensais(int meses) {
        log.info("Buscando sinistros dos últimos {} meses", meses);

        // Validação do parâmetro
        if (meses < 1 || meses > 24) {
            log.warn("Parâmetro 'meses' inválido: {}. Deve estar entre 1 e 24", meses);
            throw new InvalidResourceStateException(
                    "O parâmetro 'meses' deve estar entre 1 e 24. Valor fornecido: " + meses);
        }

        try {
            List<Object[]> resultados = sinistroRepository.countSinistrosPorMes(meses);

            if (resultados == null || resultados.isEmpty()) {
                log.warn("Nenhum sinistro encontrado nos últimos {} meses", meses);
                return Collections.emptyList();
            }

            return resultados.stream()
                    .map(r -> {
                        try {
                            Integer ano = ((Number) r[0]).intValue();
                            Integer mes = ((Number) r[1]).intValue();
                            Integer quantidade = ((Number) r[2]).intValue();

                            return SinistroMensalDTO.builder()
                                    .mes(getMesAbreviado(ano, mes))
                                    .sinistros(quantidade)
                                    .build();
                        } catch (Exception e) {
                            log.error("Erro ao processar dados mensais: {}", e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao buscar sinistros mensais: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao buscar sinistros mensais: " + e.getMessage());
        }
    }

    public List<SinistroRecenteDTO> getSinistrosRecentes(int limit) {
        log.info("Buscando {} sinistros mais recentes", limit);

        // Validação do parâmetro
        if (limit < 1 || limit > 100) {
            log.warn("Parâmetro 'limit' inválido: {}. Deve estar entre 1 e 100", limit);
            throw new InvalidResourceStateException(
                    "O parâmetro 'limit' deve estar entre 1 e 100. Valor fornecido: " + limit);
        }

        try {
            List<Object[]> resultados = sinistroRepository.findSinistrosRecentes(limit);

            if (resultados == null || resultados.isEmpty()) {
                log.warn("Nenhum sinistro recente encontrado");
                return Collections.emptyList();
            }

            return resultados.stream()
                    .map(r -> {
                        try {
                            return SinistroRecenteDTO.builder()
                                    .id(((Number) r[0]).longValue())
                                    .cliente((String) r[1])
                                    .veiculo((String) r[2])
                                    .data((String) r[3])
                                    .gravidade((String) r[4])
                                    .status((String) r[5])
                                    .build();
                        } catch (Exception e) {
                            log.error("Erro ao processar sinistro recente: {}", e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao buscar sinistros recentes: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao buscar sinistros recentes: " + e.getMessage());
        }
    }

    public List<SinistroCategoriaDTO> getSinistrosPorCategoria() {
        log.info("Buscando distribuição de sinistros por categoria");

        try {
            List<Object[]> resultados = sinistroRepository.countSinistrosPorCategoria();

            if (resultados == null || resultados.isEmpty()) {
                log.warn("Nenhuma categoria de sinistro encontrada");
                return Collections.emptyList();
            }

            return resultados.stream()
                    .map(r -> {
                        try {
                            String categoria = (String) r[0];
                            Integer quantidade = ((Number) r[1]).intValue();

                            return SinistroCategoriaDTO.builder()
                                    .categoria(formatarCategoria(categoria))
                                    .qtd(quantidade)
                                    .build();
                        } catch (Exception e) {
                            log.error("Erro ao processar categoria: {}", e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Erro ao buscar sinistros por categoria: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao buscar sinistros por categoria: " + e.getMessage());
        }
    }

    private String getMesAbreviado(int ano, int mes) {
        try {
            LocalDate data = LocalDate.of(ano, mes, 1);
            String mesNome = data.getMonth()
                    .getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
            return mesNome.substring(0, 1).toUpperCase() + mesNome.substring(1, 3).toLowerCase();
        } catch (Exception e) {
            log.error("Erro ao formatar mês: {}/{}", mes, ano, e);
            return "---";
        }
    }

    private String formatarCategoria(String categoria) {
        if (categoria == null || categoria.isEmpty()) {
            return "Outros";
        }

        // Converte para Title Case
        String[] palavras = categoria.toLowerCase().split("_");
        StringBuilder resultado = new StringBuilder();

        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                resultado.append(Character.toUpperCase(palavra.charAt(0)))
                        .append(palavra.substring(1))
                        .append(" ");
            }
        }

        return resultado.toString().trim();
    }
}