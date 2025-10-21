package com.hermes.hermes.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeocodingService {


    private final WebClient webClient;


    public GeocodingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://geocode.xyz").build();
    }

    // Método para obter coordenadas (latitude e longitude) a partir de um endereço
    public Map<String, Double> getCoordinates(String address) {
        try {
            Mono<String> responseMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/{address}")
                            .queryParam("json", "1")
                            .build(address))
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(15));

            String responseBody = responseMono.block();

            if (responseBody == null) {
                throw new RuntimeException("A resposta da API de geocoding foi nula.");
            }

            JSONObject jsonResponse = new JSONObject(responseBody);

            if (jsonResponse.has("error")) {
                 System.err.println("A API retornou um erro: " + jsonResponse.getJSONObject("error").getString("description"));
                 Thread.sleep(2000); 
                 return getCoordinates(address);
            }

            if (jsonResponse.has("latt") && jsonResponse.get("latt") instanceof String && jsonResponse.getString("latt").contains("Throttled")) {
                System.err.println("Atingiu o limite de requisições da API. Tentando novamente em 3 segundos...");
                Thread.sleep(3000);
                return getCoordinates(address); 
            }

            double latitude = jsonResponse.getDouble("latt");
            double longitude = jsonResponse.getDouble("longt");

            Map<String, Double> coordinates = new HashMap<>();
            coordinates.put("latitude", latitude);
            coordinates.put("longitude", longitude);

            return coordinates;

        } catch (Exception e) {
            System.err.println("Erro ao obter coordenadas para o endereço: " + address);
            e.printStackTrace();
            throw new RuntimeException("Não foi possível obter as coordenadas para o endereço fornecido.", e);
        }
    }
}
