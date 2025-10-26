package com.hermes.hermes.service;

import com.hermes.hermes.controller.dto.*;
import com.hermes.hermes.controller.dto.dashboard.*;
import com.hermes.hermes.repository.ClienteRepository;
import com.hermes.hermes.repository.OficinaRepository;
import com.hermes.hermes.repository.SeguradoraRepository;
import com.hermes.hermes.repository.SinistroRepository;
import com.hermes.hermes.repository.*;
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

        Long totalClientes = clienteRepository.count();
        Long totalSinistros = sinistroRepository.count();
        Long totalOficinas = oficinaRepository.count();
        Long sinistrosComFeridos = sinistroRepository.countByFeridosTrue();
        Long sinistrosAtivos = sinistroRepository.countSinistrosAtivos();

        // Buscar contagem por gravidade
        Long leve = sinistroRepository.countByGravidade("BAIXA");
        Long moderada = sinistroRepository.countByGravidade("MODERADA");
        Long grave = sinistroRepository.countByGravidade("GRAVE");

        // Garantir que valores não sejam null
        leve = leve != null ? leve : 0L;
        moderada = moderada != null ? moderada : 0L;
        grave = grave != null ? grave : 0L;

        GravidadeStatsDTO gravidade = GravidadeStatsDTO.builder()
                .leve(leve)
                .moderada(moderada)
                .grave(grave)
                .build();

        return DashboardStatsDTO.builder()
                .totalClientes(totalClientes.intValue())
                .totalSinistros(totalSinistros.intValue())
                .totalOficinas(totalOficinas.intValue())
                .sinistrosComFeridos(sinistrosComFeridos != null ? sinistrosComFeridos.intValue() : 0)
                .sinistrosAtivos(sinistrosAtivos != null ? sinistrosAtivos.intValue() : 0)
                .gravidade(gravidade)
                .build();
    }

    public List<SinistroMensalDTO> getSinistrosMensais(int meses) {
        log.info("Buscando sinistros dos últimos {} meses", meses);

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
    }

    public List<SinistroRecenteDTO> getSinistrosRecentes(int limit) {
        log.info("Buscando {} sinistros mais recentes", limit);

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
    }

    public List<SinistroCategoriaDTO> getSinistrosPorCategoria() {
        log.info("Buscando distribuição de sinistros por categoria");

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