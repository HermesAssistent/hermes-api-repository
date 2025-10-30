package com.hermes.hermes.service;

import com.hermes.hermes.domain.model.localizacao.Localizacao;
import com.hermes.hermes.exception.GeocodingException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class GeocodingService {


    private final WebClient brasilClient;
    private final WebClient geocodeClient;

    public GeocodingService(WebClient.Builder webClientBuilder) {
        this.brasilClient = webClientBuilder.baseUrl("https://brasilapi.com.br/api").build();
        this.geocodeClient = webClientBuilder.baseUrl("https://geocode.xyz").build();
    }

    public Localizacao getCoordinates(String cep) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
        // Buscar endereço e CEP usando BrasilAPI
        Mono<String> brasilApiResponse = brasilClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/cep/v1/{cep}")
                .build(cep))
            .retrieve()
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(20))
            .onErrorResume(throwable -> {
                System.err.println("Erro ao acessar BrasilAPI: " + throwable.getMessage());
                return Mono.empty();
            });

                String brasilApiBody = brasilApiResponse.block();

                if (brasilApiBody == null) {
                    throw new GeocodingException("A resposta da BrasilAPI foi nula. Verifique a conectividade ou o CEP fornecido.");
                }

                JSONObject brasilApiJson = new JSONObject(brasilApiBody);

                if (brasilApiJson.has("erro") && brasilApiJson.getBoolean("erro")) {
                    throw new GeocodingException("CEP não encontrado na BrasilAPI. CEP fornecido: " + cep);
                }

        String street = brasilApiJson.has("street") ? brasilApiJson.optString("street", "")
            : brasilApiJson.optString("logradouro", "");
        String neighborhood = brasilApiJson.has("neighborhood") ? brasilApiJson.optString("neighborhood", "")
            : brasilApiJson.optString("bairro", "");
        String city = brasilApiJson.has("city") ? brasilApiJson.optString("city", "")
            : brasilApiJson.optString("localidade", "");
        String state = brasilApiJson.has("state") ? brasilApiJson.optString("state", "")
            : brasilApiJson.optString("uf", "");

        StringBuilder enderecoBuilder = new StringBuilder();
        if (!street.isBlank()) enderecoBuilder.append(street);
        if (!neighborhood.isBlank()) {
            if (enderecoBuilder.length() > 0) enderecoBuilder.append(", ");
            enderecoBuilder.append(neighborhood);
        }
        if (!city.isBlank()) {
            if (enderecoBuilder.length() > 0) enderecoBuilder.append(", ");
            enderecoBuilder.append(city);
        }
        if (!state.isBlank()) {
            if (enderecoBuilder.length() > 0) enderecoBuilder.append(" - ");
            enderecoBuilder.append(state);
        }

        String endereco = enderecoBuilder.toString();

        Mono<String> geocodeResponse = geocodeClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/{address}")
                .queryParam("json", "1")
                .build(endereco))
            .retrieve()
            .bodyToMono(String.class)
            .timeout(Duration.ofSeconds(20))
            .onErrorResume(throwable -> {
                System.err.println("Erro ao acessar geocode.xyz: " + throwable.getMessage());
                return Mono.empty();
            });

                String geocodeBody = geocodeResponse.block();

                Double latitude = null;
                Double longitude = null;

                if (geocodeBody != null) {
                    JSONObject geocodeJson = new JSONObject(geocodeBody);

                    if (geocodeJson.has("error") || !geocodeJson.has("latt") || !geocodeJson.has("longt")) {
                        String errorMessage = geocodeJson.has("error")
                            ? geocodeJson.getJSONObject("error").optString("description", "Erro desconhecido")
                            : "Resposta inesperada da API geocode.xyz. Continuando com os dados da BrasilAPI.";
                        System.err.println("Aviso: " + errorMessage);
                    } else {
                        try {
                            latitude = Double.parseDouble(geocodeJson.getString("latt"));
                            longitude = Double.parseDouble(geocodeJson.getString("longt"));
                        } catch (NumberFormatException e) {
                            System.err.println("Erro ao converter coordenadas retornadas pela geocode.xyz. Continuando com os dados da BrasilAPI. Resposta: " + geocodeJson.toString());
                        }
                    }
                } else {
                    System.err.println("Aviso: Resposta nula da geocode.xyz. Continuando com os dados da BrasilAPI.");
                }

                return new Localizacao(endereco, latitude, longitude, cep);

            } catch (GeocodingException e) {
                attempt++;
                System.err.println("Erro específico na tentativa " + attempt + " de " + maxRetries + ": " + e.getMessage());

                if (attempt >= maxRetries) {
                    throw new GeocodingException("Não foi possível obter os dados para o CEP fornecido após " + maxRetries + " tentativas. Último erro: " + e.getMessage(), e);
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new GeocodingException("Thread interrompida durante a espera entre tentativas.", ie);
                }
            } catch (Exception e) {
                throw new GeocodingException("Erro inesperado ao processar o CEP: " + cep + ". Detalhes: " + e.getMessage(), e);
            }
        }

        throw new GeocodingException("Erro inesperado ao obter os dados para o CEP fornecido.");
    }
}
