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
        log.info("Buscando estatísticas do dashboard");
        DashboardStatsDTO stats = dashboardService.getStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/sinistros-mensais")
    public ResponseEntity<List<SinistroMensalDTO>> getSinistrosMensais(
            @RequestParam(defaultValue = "6") int meses) {
        log.info("Buscando sinistros mensais dos últimos {} meses", meses);
        List<SinistroMensalDTO> sinistros = dashboardService.getSinistrosMensais(meses);
        return ResponseEntity.ok(sinistros);
    }

    @GetMapping("/sinistros-recentes")
    public ResponseEntity<List<SinistroRecenteDTO>> getSinistrosRecentes(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Buscando {} sinistros mais recentes", limit);
        List<SinistroRecenteDTO> sinistros = dashboardService.getSinistrosRecentes(limit);
        return ResponseEntity.ok(sinistros);
    }

    @GetMapping("/sinistros-categoria")
    public ResponseEntity<List<SinistroCategoriaDTO>> getSinistrosPorCategoria() {
        log.info("Buscando distribuição de sinistros por categoria");
        List<SinistroCategoriaDTO> categorias = dashboardService.getSinistrosPorCategoria();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Dashboard API está funcionando!");
    }
}