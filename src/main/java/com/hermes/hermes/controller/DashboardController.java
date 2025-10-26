package com.hermes.hermes.controller;

import com.hermes.hermes.controller.dto.dashboard.*;

import com.hermes.hermes.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        try {
            log.info("Buscando estatísticas do dashboard");
            DashboardStatsDTO stats = dashboardService.getStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Erro ao buscar estatísticas do dashboard", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sinistros-mensais")
    public ResponseEntity<List<SinistroMensalDTO>> getSinistrosMensais(@RequestParam(defaultValue = "6") int meses) {
        try {
            log.info("Buscando sinistros mensais dos últimos {} meses", meses);

            if (meses < 1 || meses > 24) {
                log.warn("Parâmetro 'meses' inválido: {}. Usando valor padrão: 6", meses);
                meses = 6;
            }

            List<SinistroMensalDTO> sinistros = dashboardService.getSinistrosMensais(meses);
            return ResponseEntity.ok(sinistros);
        } catch (Exception e) {
            log.error("Erro ao buscar sinistros mensais", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sinistros-recentes")
    public ResponseEntity<List<SinistroRecenteDTO>> getSinistrosRecentes(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            log.info("Buscando {} sinistros mais recentes", limit);

            if (limit < 1 || limit > 100) {
                log.warn("Parâmetro 'limit' inválido: {}. Usando valor padrão: 10", limit);
                limit = 10;
            }

            List<SinistroRecenteDTO> sinistros = dashboardService.getSinistrosRecentes(limit);
            return ResponseEntity.ok(sinistros);
        } catch (Exception e) {
            log.error("Erro ao buscar sinistros recentes", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sinistros-categoria")
    public ResponseEntity<List<SinistroCategoriaDTO>> getSinistrosPorCategoria() {
        try {
            log.info("Buscando distribuição de sinistros por categoria");
            List<SinistroCategoriaDTO> categorias = dashboardService.getSinistrosPorCategoria();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            log.error("Erro ao buscar sinistros por categoria", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Dashboard API está funcionando!");
    }
}