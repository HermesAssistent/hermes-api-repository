package com.hermes.hermes.service;

import com.hermes.hermes.exception.LLMGenerationException;
import com.hermes.hermes.exception.SQLExecutionException;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.OllamaResult;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class LLMQueryService {
    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    private static final int REQUEST_TIMEOUT_SECONDS = 60;
    private static final String MODEL_SQLCODER = "sqlcoder";
    private static final String MODEL_LLAMA = "llama3.1";
    private static final int MAX_RETRY_ATTEMPTS = 2;

    private final JdbcTemplate jdbcTemplate;
    private final OllamaAPI ollama;
    private final SchemaContextGenerator schemaGenerator;
    private final SQLCleaner sqlCleaner;
    private final ResultFormatter resultFormatter;

    public LLMQueryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.ollama = new OllamaAPI(OLLAMA_BASE_URL);
        this.ollama.setRequestTimeoutSeconds(REQUEST_TIMEOUT_SECONDS);
        this.schemaGenerator = new SchemaContextGenerator();
        this.sqlCleaner = new SQLCleaner();
        this.resultFormatter = new ResultFormatter();
    }

    public String responderPergunta(String pergunta) {
        String schemaContext = schemaGenerator.gerarContextoSchemaCompleto();
        String sql = gerarSQL(pergunta, schemaContext);

        log.info("SQL Final Gerado: {}", sql);

        return executarConsulta(sql);
    }

    private String gerarSQL(String pergunta, String schemaContext) {
        String sql = tentarGerarComSQLCoder(pergunta, schemaContext);

        if (sql == null) {
            sql = tentarGerarComLlama(pergunta, schemaContext);
        }

        if (sql == null || !isSQLValido(sql)) {
            log.warn("Usando fallback automático para pergunta: {}", pergunta);
            return gerarFallbackSQL(pergunta);
        }

        if (sql != null &&  sql.contains(";")) {
            sql = sql.substring(0, sql.indexOf(";"));
        }

        return sql;
    }

    private String tentarGerarComSQLCoder(String pergunta, String schemaContext) {
        try {
            String sql = gerarSQLComSQLCoder(pergunta, schemaContext);

            if (!isSQLValido(sql)) {
                sql = corrigirSQL(sql, schemaContext, pergunta, MODEL_SQLCODER);
            }

            return isSQLValido(sql) ? sql : null;
        } catch (Exception e) {
            log.warn("SQLCoder falhou: {}", e.getMessage());
            return null;
        }
    }

    private String tentarGerarComLlama(String pergunta, String schemaContext) {
        try {
            String sql = gerarSQLComLlama(pergunta, schemaContext);

            if (!isSQLValido(sql)) {
                sql = corrigirSQL(sql, schemaContext, pergunta, MODEL_LLAMA);
            }

            return isSQLValido(sql) ? sql : null;
        } catch (Exception e) {
            log.warn("Llama falhou: {}", e.getMessage());
            return null;
        }
    }

    private String gerarSQLComSQLCoder(String pergunta, String schemaContext) throws Exception {
        String condicoesTraduzidas = extrairCondicoesDaPergunta(pergunta);

        String prompt = """
            As a SQL expert, generate a PostgreSQL SELECT query based on the database schema and user question.
            
            DATABASE SCHEMA:
            %s
            
            USER QUESTION: "%s"
            
            PORTUGUESE TO SQL CONTEXT:
            - "quantos" = count how many
            - "listar" = list/show all
            - "clientes" = customers (table: cliente)
            - "sinistros" = insurance claims (table: sinistro)
            - "oficinas" = workshops (table: oficina)
            - "seguradoras" = insurance companies (table: seguradora)
            - "feridos" = injured people (use column: ferido which is BOOLEAN)
            - "houve" = there were/happened
            - "com" = with
            - "onde" = where
            - "que" = that/which
            
            SPECIFIC CONDITIONS FROM QUESTION:
            %s
            
            CRITICAL NOTES:
            - The column for injuries is 'ferido' (BOOLEAN type), not 'feridos'
            - For boolean columns, use: WHERE ferido = true (not WHERE feridos = 't')
            - Use COUNT(*) for counting records
            - Use exact column names from schema
            
            REQUIREMENTS:
            - Return ONLY the executable SQL query, nothing else
            - Use PostgreSQL syntax
            - Only SELECT statements are allowed
            - Include semicolon at the end
            - Use exact table and column names from the schema
            - For counts, use COUNT(*)
            - Use proper JOIN syntax when needed
            - Add WHERE clauses when the question specifies conditions
            - For boolean conditions, use = true or = false (not strings like 't' or 'f')
            
            SQL QUERY:
            """.formatted(schemaContext, pergunta, condicoesTraduzidas);

        return executarLLM(MODEL_SQLCODER, prompt);
    }

    private String gerarSQLComLlama(String pergunta, String schemaContext) throws Exception {
        String prompt = """
            Com base no schema abaixo, gere UMA consulta SQL para PostgreSQL.
            
            SCHEMA:
            %s
            
            PERGUNTA: "%s"
            
            REGRAS:
            - Retorne APENAS a query SQL, nada mais
            - Sem explicações, sem markdown, sem JSON
            - Use a sintaxe PostgreSQL
            - Query deve ser executável diretamente
            - Apenas SELECT é permitido
            - Termine com ponto e vírgula
            - Adicione cláusulas WHERE quando a pergunta especificar condições
            
            SQL:
            """.formatted(schemaContext, pergunta);

        return executarLLM(MODEL_LLAMA, prompt);
    }

    private String corrigirSQL(String sqlInvalido, String schemaContext, String pergunta, String modelo) throws Exception {
        String sqlCorrigido = sqlCleaner.corrigirProblemasComuns(sqlInvalido);

        if (isSQLValido(sqlCorrigido)) {
            return sqlCorrigido;
        }

        String promptCorrecao = modelo.equals(MODEL_SQLCODER)
                ? criarPromptCorrecaoSQLCoder(sqlInvalido, schemaContext, pergunta)
                : criarPromptCorrecaoLlama(sqlInvalido, schemaContext, pergunta);

        return executarLLM(modelo, promptCorrecao);
    }

    private String criarPromptCorrecaoSQLCoder(String sqlInvalido, String schemaContext, String pergunta) {
        return """
            Correct this invalid SQL query for PostgreSQL.
            
            SCHEMA:
            %s
            
            ORIGINAL QUESTION: "%s"
            
            INVALID SQL: 
            %s
            
            CORRECTIONS NEEDED:
            - Fix any syntax errors
            - Ensure it starts with SELECT
            - Use proper table/column names from schema
            - End with semicolon
            - Make it executable in PostgreSQL
            - Add WHERE clauses if the original question specified conditions
            - For boolean columns, use = true or = false (not strings like 't' or 'f')
            
            Return ONLY the corrected SQL query:
            """.formatted(schemaContext, pergunta, sqlInvalido);
    }

    private String criarPromptCorrecaoLlama(String sqlInvalido, String schemaContext, String pergunta) {
        return """
            Corrija esta consulta SQL inválida para PostgreSQL.
            
            SCHEMA:
            %s
            
            PERGUNTA ORIGINAL: "%s"
            
            SQL INVÁLIDO:
            %s
            
            CORREÇÕES NECESSÁRIAS:
            - Corrija erros de sintaxe
            - Garanta que comece com SELECT
            - Use nomes corretos de tabelas/colunas do schema
            - Termine com ponto e vírgula
            - Torne executável no PostgreSQL
            - Adicione cláusulas WHERE se a pergunta original pedia condições
            
            Retorne APENAS o SQL corrigido:
            """.formatted(schemaContext, pergunta, sqlInvalido);
    }

    private String executarLLM(String modelo, String prompt) throws Exception {
        try {
            Map<String, Object> mapResult = new HashMap<>();
            OllamaResult result = ollama.generate(modelo, prompt, mapResult);

            String response = result.getResponse();
            if (response == null || response.trim().isEmpty()) {
                throw new LLMGenerationException("LLM retornou resposta vazia para modelo: " + modelo);
            }

            return sqlCleaner.limparResposta(response, modelo);
        } catch (Exception e) {
            throw new LLMGenerationException("Erro ao executar LLM " + modelo, e);
        }
    }

    private String extrairCondicoesDaPergunta(String pergunta) {
        pergunta = pergunta.toLowerCase();
        StringBuilder condicoes = new StringBuilder();

        Map<String, String> mapeamentoCondicoes = Map.ofEntries(
                Map.entry("feridos", "ferido = true (BOOLEAN column)"),
                Map.entry("ferido", "ferido = true (BOOLEAN column)"),
                Map.entry("machucados", "ferido = true (BOOLEAN column)"),
                Map.entry("lesionados", "ferido = true (BOOLEAN column)"),
                Map.entry("ativos", "ativo = true (BOOLEAN column)"),
                Map.entry("inativos", "ativo = false (BOOLEAN column)"),
                Map.entry("finalizados", "status = 'FINALIZADO'"),
                Map.entry("pendentes", "status = 'PENDENTE'"),
                Map.entry("abertos", "status = 'ABERTO'"),
                Map.entry("este mês", "datahora_criacao >= DATE_TRUNC('month', CURRENT_DATE)"),
                Map.entry("este ano", "datahora_criacao >= DATE_TRUNC('year', CURRENT_DATE)"),
                Map.entry("hoje", "datahora_criacao >= CURRENT_DATE")
        );

        for (Map.Entry<String, String> entry : mapeamentoCondicoes.entrySet()) {
            if (pergunta.contains(entry.getKey())) {
                condicoes.append("- ").append(entry.getKey()).append(" → ").append(entry.getValue()).append("\n");
            }
        }

        if (pergunta.contains("houve") && pergunta.contains("ferido")) {
            condicoes.append("- Question asks about accidents WITH injuries → WHERE ferido = true\n");
            condicoes.append("- IMPORTANT: Use COUNT(*) to count records\n");
        }

        if (pergunta.contains("sem") && pergunta.contains("ferido")) {
            condicoes.append("- Question asks about accidents WITHOUT injuries → WHERE ferido = false\n");
            condicoes.append("- IMPORTANT: Use COUNT(*) to count records\n");
        }

        if (pergunta.contains("ferido")) {
            condicoes.append("- BOOLEAN COLUMN: Use 'ferido = true' or 'ferido = false', not strings\n");
        }

        return condicoes.toString();
    }

    private boolean isSQLValido(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        String cleanSql = sql.toUpperCase().trim();

        if (!cleanSql.startsWith("SELECT") || !cleanSql.contains("FROM")) {
            return false;
        }

        if (cleanSql.contains("\"SELECT") ||
                cleanSql.matches(".*\\{\\s*\"SELECT.*") ||
                cleanSql.matches(".*\"\\s*:\\s*\\{.*")) {
            return false;
        }

        return cleanSql.length() >= 10;
    }

    private String gerarFallbackSQL(String pergunta) {
        pergunta = pergunta.toLowerCase();

        if (pergunta.contains("quantos") && pergunta.contains("sinistro")) {
            if (pergunta.contains("ferido")) {
                return "SELECT COUNT(*) FROM sinistro WHERE feridos = true;";
            } else if (pergunta.contains("ativo")) {
                return "SELECT COUNT(*) FROM sinistro WHERE ativo = true;";
            } else {
                return "SELECT COUNT(*) FROM sinistro;";
            }
        } else if (pergunta.contains("listar") && pergunta.contains("sinistro")) {
            if (pergunta.contains("ferido")) {
                return "SELECT * FROM sinistro WHERE feridos = true LIMIT 10;";
            } else {
                return "SELECT * FROM sinistro LIMIT 10;";
            }
        } else if (pergunta.contains("listar") && pergunta.contains("cliente")) {
            return "SELECT * FROM cliente LIMIT 10;";
        } else if (pergunta.contains("quantos") && pergunta.contains("cliente")) {
            return "SELECT COUNT(*) FROM cliente;";
        } else if (pergunta.contains("todos") && pergunta.contains("cliente")) {
            return "SELECT * FROM cliente LIMIT 10;";
        } else if (pergunta.contains("oficina")) {
            return "SELECT COUNT(*) FROM oficina;";
        } else if (pergunta.contains("seguradora")) {
            return "SELECT COUNT(*) FROM seguradora;";
        } else {
            return "SELECT COUNT(*) FROM cliente;";
        }
    }

    private String executarConsulta(String sql) {
        try {
            var resultado = jdbcTemplate.queryForList(sql);
            return resultFormatter.formatarResultado(resultado);
        } catch (Exception e) {
            log.error("Erro ao executar SQL: {}", sql, e);
            throw new SQLExecutionException("Erro ao executar a consulta", sql, e);
        }
    }

    // Classes internas para melhor organização
    private class SchemaContextGenerator {
        public String gerarContextoSchemaCompleto() {
            List<Class<?>> entidades = List.of(
                    com.hermes.hermes.domain.model.cliente.Cliente.class,
                    com.hermes.hermes.domain.model.sinistro.Sinistro.class,
                    com.hermes.hermes.domain.model.oficina.Oficina.class,
                    com.hermes.hermes.domain.model.seguradora.Seguradora.class,
                    com.hermes.hermes.domain.model.usuario.Usuario.class,
                    com.hermes.hermes.domain.model.chat.ChatMessage.class,
                    com.hermes.hermes.domain.model.chat.ChatSession.class
            );

            StringBuilder sb = new StringBuilder();

            for (Class<?> entidade : entidades) {
                if (!entidade.isAnnotationPresent(Entity.class)) continue;

                String nomeTabela = extrairNomeTabela(entidade);
                sb.append("Tabela: ").append(nomeTabela).append("\n");
                sb.append("Descrição: ").append(obterDescricaoTabela(entidade)).append("\n");
                sb.append("Colunas:\n");

                for (Field campo : entidade.getDeclaredFields()) {
                    if (campo.isSynthetic() || campo.getName().contains("$")) continue;

                    String nomeColuna = extrairNomeColuna(campo);
                    String tipo = mapearTipoJpaParaSql(campo);
                    String constraints = extrairConstraints(campo);
                    String descricao = obterDescricaoCampo(campo);

                    sb.append("  - ").append(nomeColuna)
                            .append(" (").append(tipo).append(")")
                            .append(constraints);

                    if (!descricao.isEmpty()) {
                        sb.append(" - ").append(descricao);
                    }
                    sb.append("\n");
                }
                sb.append("\n");
            }

            return sb.toString();
        }

        private String obterDescricaoTabela(Class<?> entidade) {
            Map<String, String> descricoes = Map.of(
                    "sinistro", "Tabela de sinistros/acidentes de seguro",
                    "cliente", "Tabela de clientes da seguradora",
                    "oficina", "Tabela de oficinas parceiras",
                    "seguradora", "Tabela de seguradoras",
                    "usuario", "Tabela de usuários do sistema",
                    "chatmessage", "Tabela de mensagens de chat",
                    "chatsession", "Tabela de sessões de chat"
            );
            return descricoes.getOrDefault(entidade.getSimpleName().toLowerCase(), "");
        }

        private String obterDescricaoCampo(Field campo) {
            Map<String, String> descricoes = Map.ofEntries(
                    Map.entry("ferido", "Indica se houve feridos no sinistro (true/false)"),
                    Map.entry("ativo", "Indica se o registro está ativo (true/false)"),
                    Map.entry("status", "Status do registro (ABERTO, PENDENTE, FINALIZADO, etc)"),
                    Map.entry("data_criacao", "Data de criação do registro"),
                    Map.entry("data_atualizacao", "Data da última atualização"),
                    Map.entry("descricao", "Descrição detalhada"),
                    Map.entry("valor", "Valor monetário"),
                    Map.entry("nome", "Nome da entidade"),
                    Map.entry("email", "Endereço de e-mail"),
                    Map.entry("telefone", "Número de telefone")
            );
            return descricoes.getOrDefault(campo.getName().toLowerCase(), "");
        }

        private String extrairNomeTabela(Class<?> entidade) {
            if (entidade.isAnnotationPresent(Entity.class)) {
                Entity tabela = entidade.getAnnotation(Entity.class);
                if (!tabela.name().isEmpty()) {
                    return tabela.name();
                }
            }
            return entidade.getSimpleName().toLowerCase();
        }

        private String extrairNomeColuna(Field campo) {
            if (campo.isAnnotationPresent(Column.class)) {
                Column coluna = campo.getAnnotation(Column.class);
                if (!coluna.name().isEmpty()) {
                    return coluna.name();
                }
            }

            if (campo.isAnnotationPresent(ManyToOne.class)) {
                return campo.getName().toLowerCase() + "_id";
            }

            return campo.getName().toLowerCase();
        }

        private String mapearTipoJpaParaSql(Field campo) {
            Class<?> tipo = campo.getType();

            if (tipo.equals(String.class)) return "VARCHAR";
            else if (tipo.equals(Long.class) || tipo.equals(long.class)) return "BIGINT";
            else if (tipo.equals(Integer.class) || tipo.equals(int.class)) return "INTEGER";
            else if (tipo.equals(Double.class) || tipo.equals(double.class)) return "DOUBLE PRECISION";
            else if (tipo.equals(Boolean.class) || tipo.equals(boolean.class)) return "BOOLEAN";
            else if (tipo.equals(java.time.LocalDate.class)) return "DATE";
            else if (tipo.equals(java.time.LocalDateTime.class)) return "TIMESTAMP";
            else if (campo.isAnnotationPresent(ManyToOne.class)) return "BIGINT";
            else return "VARCHAR";
        }

        private String extrairConstraints(Field campo) {
            List<String> constraints = new ArrayList<>();

            if (campo.isAnnotationPresent(Id.class)) {
                constraints.add("PRIMARY KEY");
            }

            if (campo.isAnnotationPresent(Column.class)) {
                Column coluna = campo.getAnnotation(Column.class);
                if (!coluna.nullable()) {
                    constraints.add("NOT NULL");
                }
            }

            return constraints.isEmpty() ? "" : " [" + String.join(", ", constraints) + "]";
        }
    }

    private class SQLCleaner {
        public String corrigirProblemasComuns(String sql) {
            String corrigido = sql;

            corrigido = corrigido.replace("feridos", "ferido");
            corrigido = corrigido.replace("ferido = 't'", "ferido = true")
                    .replace("ferido = 'T'", "ferido = true")
                    .replace("ferido = 'f'", "ferido = false")
                    .replace("ferido = 'F'", "ferido = false");

            Pattern pattern = Pattern.compile("SELECT \\* FROM sinistro WHERE ferido", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(corrigido);
            if (matcher.find()) {
                corrigido = corrigido.replace("SELECT *", "SELECT COUNT(*)");
            }

            return ensureSemicolon(corrigido);
        }

        public String limparResposta(String resposta, String modelo) {
            if (resposta == null) return "SELECT COUNT(*) FROM cliente;";

            if (modelo.equals(MODEL_SQLCODER)) {
                return limparRespostaSQLCoder(resposta);
            } else {
                return limparRespostaLlama(resposta);
            }
        }

        private String limparRespostaSQLCoder(String resposta) {
            String[] lines = resposta.split("\n");
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.toUpperCase().startsWith("SELECT") &&
                        !trimmed.contains("```") &&
                        !trimmed.startsWith("--")) {
                    return ensureSemicolon(trimmed);
                }
            }

            String clean = resposta.replaceAll("```sql", "")
                    .replaceAll("```", "")
                    .replaceAll("\"", "")
                    .trim();
            return ensureSemicolon(clean);
        }

        private String limparRespostaLlama(String resposta) {
            String clean = resposta.replaceAll("```sql", "")
                    .replaceAll("```", "")
                    .replaceAll("\"", "")
                    .replaceAll("\\{", "")
                    .replaceAll("\\}", "")
                    .trim();

            Pattern pattern = Pattern.compile("(SELECT.*?;)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(clean);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }

            String[] lines = clean.split("\n");
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.toUpperCase().startsWith("SELECT")) {
                    return ensureSemicolon(trimmed);
                }
            }

            return ensureSemicolon(clean);
        }

        private String ensureSemicolon(String sql) {
            if (sql == null) return "SELECT COUNT(*) FROM cliente;";

            String trimmed = sql.trim();
            if (!trimmed.endsWith(";")) {
                return trimmed + ";";
            }
            return trimmed;
        }
    }

    private class ResultFormatter {
        public String formatarResultado(List<Map<String, Object>> resultado) {
            if (resultado.isEmpty()) {
                return "📭 Nenhum resultado encontrado na consulta.";
            }

            StringBuilder sb = new StringBuilder();

            if (resultado.size() == 1) {
                return formatarResultadoUnico(resultado.get(0), sb);
            } else {
                return formatarMultiplosResultados(resultado, sb);
            }
        }

        private String formatarResultadoUnico(Map<String, Object> registro, StringBuilder sb) {
            if (registro.size() == 1) {
                Map.Entry<String, Object> entry = registro.entrySet().iterator().next();
                String chave = entry.getKey().toLowerCase();
                Object valor = entry.getValue();

                if (chave.contains("count") || chave.contains("quantidade") || chave.contains("total")) {
                    return formatarResultadoCount(valor);
                }
            }

            sb.append("📊 **Resultado da consulta:**\n\n");
            for (Map.Entry<String, Object> entry : registro.entrySet()) {
                String chave = formatarNomeColuna(entry.getKey());
                Object valor = entry.getValue();
                String emoji = getEmojiParaColuna(chave);
                sb.append(emoji).append(" **").append(chave).append(":** ").append(formatarValor(valor)).append("\n");
            }

            return sb.toString();
        }

        private String formatarResultadoCount(Object valor) {
            if (valor instanceof Number) {
                int numero = ((Number) valor).intValue();
                if (numero == 0) {
                    return "🔍 Não foram encontrados registros.";
                } else if (numero == 1) {
                    return "✅ Foi encontrado 1 registro.";
                } else {
                    return "✅ Foram encontrados " + numero + " registros.";
                }
            }
            return "✅ Resultado: " + valor;
        }

        private String formatarMultiplosResultados(List<Map<String, Object>> resultado, StringBuilder sb) {
            sb.append("📋 **").append(resultado.size()).append(" registros encontrados:**\n\n");

            Map<String, Object> primeiro = resultado.get(0);
            List<String> colunas = new ArrayList<>(primeiro.keySet());

            for (int i = 0; i < resultado.size(); i++) {
                sb.append("**📄 Registro ").append(i + 1).append(":**\n");
                Map<String, Object> registro = resultado.get(i);

                for (String coluna : colunas) {
                    String nomeFormatado = formatarNomeColuna(coluna);
                    Object valor = registro.get(coluna);
                    String emoji = getEmojiParaColuna(coluna);
                    sb.append("  ").append(emoji).append(" **").append(nomeFormatado).append(":** ")
                            .append(formatarValor(valor)).append("\n");
                }

                if (i < resultado.size() - 1) {
                    sb.append("\n");
                }
            }

            return sb.toString();
        }

        private String formatarNomeColuna(String nomeColuna) {
            Map<String, String> traducoes = Map.ofEntries(
                    Map.entry("count", "Quantidade"),
                    Map.entry("count_", "Quantidade"),
                    Map.entry("id", "ID"),
                    Map.entry("nome", "Nome"),
                    Map.entry("email", "E-mail"),
                    Map.entry("telefone", "Telefone"),
                    Map.entry("endereco", "Endereço"),
                    Map.entry("endereço", "Endereço"),
                    Map.entry("data_criacao", "Data de Criação"),
                    Map.entry("data_atualizacao", "Data de Atualização"),
                    Map.entry("data_nascimento", "Data de Nascimento"),
                    Map.entry("descricao", "Descrição"),
                    Map.entry("valor", "Valor"),
                    Map.entry("status", "Status"),
                    Map.entry("ativo", "Ativo"),
                    Map.entry("tipo", "Tipo"),
                    Map.entry("cpf", "CPF"),
                    Map.entry("cnpj", "CNPJ"),
                    Map.entry("placa", "Placa"),
                    Map.entry("veiculo", "Veículo"),
                    Map.entry("sinistro", "Sinistro"),
                    Map.entry("cliente", "Cliente"),
                    Map.entry("oficina", "Oficina"),
                    Map.entry("seguradora", "Seguradora"),
                    Map.entry("usuario", "Usuário"),
                    Map.entry("ferido", "Com Feridos")
            );

            String nomeLower = nomeColuna.toLowerCase();
            for (Map.Entry<String, String> entry : traducoes.entrySet()) {
                if (nomeLower.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }

            return Arrays.stream(nomeColuna.split("_"))
                    .map(palavra -> {
                        if (palavra.isEmpty()) return "";
                        return palavra.substring(0, 1).toUpperCase() + palavra.substring(1).toLowerCase();
                    })
                    .reduce((a, b) -> a + " " + b)
                    .orElse(nomeColuna);
        }

        private String formatarValor(Object valor) {
            if (valor == null) {
                return "Não informado";
            }

            if (valor instanceof java.time.LocalDateTime) {
                java.time.LocalDateTime data = (java.time.LocalDateTime) valor;
                return data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));
            }

            if (valor instanceof java.time.LocalDate) {
                java.time.LocalDate data = (java.time.LocalDate) valor;
                return data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            if (valor instanceof Boolean) {
                return (Boolean) valor ? "✅ Sim" : "❌ Não";
            }

            if (valor instanceof Number) {
                Number numero = (Number) valor;
                if (numero.doubleValue() == numero.longValue()) {
                    return String.format("%,d", numero.longValue());
                } else {
                    return String.format("%,.2f", numero.doubleValue());
                }
            }

            return valor.toString();
        }

        private String getEmojiParaColuna(String nomeColuna) {
            Map<String, String> emojis = Map.ofEntries(
                    Map.entry("id", "🆔"),
                    Map.entry("nome", "👤"),
                    Map.entry("email", "📧"),
                    Map.entry("telefone", "📞"),
                    Map.entry("endereco", "🏠"),
                    Map.entry("endereço", "🏠"),
                    Map.entry("data", "📅"),
                    Map.entry("valor", "💰"),
                    Map.entry("status", "📊"),
                    Map.entry("quantidade", "🔢"),
                    Map.entry("total", "🧮"),
                    Map.entry("count", "🔢"),
                    Map.entry("ativo", "⚡"),
                    Map.entry("tipo", "🏷️"),
                    Map.entry("cpf", "📋"),
                    Map.entry("cnpj", "🏢"),
                    Map.entry("placa", "🚗"),
                    Map.entry("veiculo", "🚙"),
                    Map.entry("sinistro", "⚡"),
                    Map.entry("cliente", "👥"),
                    Map.entry("oficina", "🔧"),
                    Map.entry("seguradora", "🏛️"),
                    Map.entry("usuario", "👤"),
                    Map.entry("ferido", "🏥")
            );

            String nomeLower = nomeColuna.toLowerCase();
            for (Map.Entry<String, String> entry : emojis.entrySet()) {
                if (nomeLower.contains(entry.getKey())) {
                    return entry.getValue() + " ";
                }
            }

            return "• ";
        }
    }
}