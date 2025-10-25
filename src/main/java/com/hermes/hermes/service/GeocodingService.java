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


    private final WebClient webClient;


    public GeocodingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://brasilapi.com.br/api").build();
    }

    public Localizacao getCoordinates(String cep) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                // Buscar endereço e CEP usando BrasilAPI
                Mono<String> brasilApiResponse = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/cep/v1/{cep}")
                                .build(cep))
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(15));

                String brasilApiBody = brasilApiResponse.block();

                if (brasilApiBody == null) {
                    throw new GeocodingException("A resposta da BrasilAPI foi nula. Verifique a conectividade ou o CEP fornecido.");
                }

                JSONObject brasilApiJson = new JSONObject(brasilApiBody);

                if (brasilApiJson.has("erro") && brasilApiJson.getBoolean("erro")) {
                    throw new GeocodingException("CEP não encontrado na BrasilAPI. CEP fornecido: " + cep);
                }

                String endereco = brasilApiJson.getString("logradouro") + ", " +
                                  brasilApiJson.getString("bairro") + ", " +
                                  brasilApiJson.getString("localidade") + " - " +
                                  brasilApiJson.getString("uf");

                // Buscar latitude e longitude usando geocode.xyz
                Mono<String> geocodeResponse = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/{address}")
                                .queryParam("json", "1")
                                .build(endereco))
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(15));

                String geocodeBody = geocodeResponse.block();

                if (geocodeBody == null) {
                    throw new GeocodingException("A resposta da geocode.xyz foi nula. Verifique a conectividade ou o endereço fornecido: " + endereco);
                }

                JSONObject geocodeJson = new JSONObject(geocodeBody);

                if (geocodeJson.has("error")) {
                    throw new GeocodingException("Erro ao buscar coordenadas na geocode.xyz: " + geocodeJson.getJSONObject("error").getString("description") + ". Endereço: " + endereco);
                }

                Double latitude = geocodeJson.has("latt") ? geocodeJson.getDouble("latt") : null;
                Double longitude = geocodeJson.has("longt") ? geocodeJson.getDouble("longt") : null;

                return new Localizacao(endereco, latitude, longitude, cep);

            } catch (GeocodingException e) {
                attempt++;
                System.err.println("Erro específico na tentativa " + attempt + " de " + maxRetries + ": " + e.getMessage());

                if (attempt >= maxRetries) {
                    throw new GeocodingException("Não foi possível obter os dados para o CEP fornecido após " + maxRetries + " tentativas. Último erro: " + e.getMessage(), e);
                }

                try {
                    Thread.sleep(2000); // Aguardar antes de tentar novamente
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
