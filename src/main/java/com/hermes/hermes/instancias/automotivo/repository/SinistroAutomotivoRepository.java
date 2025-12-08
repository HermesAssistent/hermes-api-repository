package com.hermes.hermes.instancias.automotivo.repository;

import com.hermes.hermes.instancias.automotivo.domain.model.SinistroAutomotivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SinistroAutomotivoRepository extends JpaRepository<SinistroAutomotivo, Long> {

    List<SinistroAutomotivo> findAllByCliente_IdIs(Long id);

    // Contagem de sinistros com feridos
    Long countByFeridosTrue();

    // Contagem por gravidade
    Long countByGravidade(String gravidade);

    Long countByGravidadeEqualsIgnoreCase(String gravidade);

    // Contagem de sinistros ativos (considerando sinistros dos últimos 30 dias como ativos)
    @Query("SELECT COUNT(s) FROM SinistroAutomotivo s WHERE s.ativo = true")
    Long countSinistrosAtivos();

    @Query(value = """
    SELECT 
        EXTRACT(YEAR FROM TO_DATE(s.data, 'YYYY-MM-DD')) as ano,
        EXTRACT(MONTH FROM TO_DATE(s.data, 'YYYY-MM-DD')) as mes,
        COUNT(*) as qtdSinistros
    FROM sinistro s
    WHERE TO_DATE(s.data, 'YYYY-MM-DD') >= CURRENT_DATE - (INTERVAL '1 month' * :meses)
    GROUP BY 
        EXTRACT(YEAR FROM TO_DATE(s.data, 'YYYY-MM-DD')),
        EXTRACT(MONTH FROM TO_DATE(s.data, 'YYYY-MM-DD'))
    ORDER BY ano, mes
    """, nativeQuery = true)
    List<Object[]> countSinistrosPorMes(@Param("meses") int meses);

    // Sinistros recentes com informações do cliente
    @Query(value = "SELECT " +
            "s.id, " +
            "COALESCE(u.nome, 'Cliente sem nome') as cliente, " +
            "COALESCE(s.modelo_veiculo, 'Veículo não informado') as veiculo, " +
            "COALESCE(s.data, 'Data não informada') as data, " +
            "COALESCE(s.gravidade, 'Não classificada') as gravidade, " +
            "CASE WHEN s.ativo = true THEN 'ABERTO' ELSE 'FINALIZADO' END as status " +
            "FROM sinistro s " +
            "LEFT JOIN cliente c ON s.cliente_id = c.id " +
            "LEFT JOIN usuario u ON c.usuario_id = u.id " +
            "ORDER BY s.datahora_cadastro DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Object[]> findSinistrosRecentes(@Param("limit") int limit);

    // Contagem de sinistros por categoria
    @Query(value = "SELECT " +
            "COALESCE(s.categoria_problema, 'Outros') as categoria, " +
            "COUNT(*) as quantidade " +
            "FROM sinistro s " +
            "GROUP BY categoria " +
            "ORDER BY quantidade DESC",
            nativeQuery = true)
    List<Object[]> countSinistrosPorCategoria();

    // Query para distribuição por gravidade
    @Query(value = "SELECT " +
            "COALESCE(s.gravidade, 'Não classificada') as gravidade, " +
            "COUNT(*) as quantidade " +
            "FROM sinistro s " +
            "GROUP BY gravidade",
            nativeQuery = true)
    List<Object[]> countSinistrosPorGravidade();

    // Query para contar sinistros com seguro
    Long countByPossuiSeguroTrue();
}
