package com.hermes.hermes.service;

import com.hermes.hermes.domain.model.oficina.Oficina;
import com.hermes.hermes.domain.model.sinistro.Sinistro;
import com.hermes.hermes.exception.NotFoundException;
import com.hermes.hermes.repository.OficinaRepository;
import com.hermes.hermes.repository.SinistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OficinaService {

    @Autowired
    private OficinaRepository oficinaRepository;

    @Autowired
    private SinistroRepository sinistroRepository;

    @Autowired
    private GeocodingService geocodingService;

    public enum OrigemBusca {
        CASA, SINISTRO, OUTRO
    }


    // Método para encontrar oficinas próximas
    public List<Oficina> findOficinasProximas(Long sinistroId, OrigemBusca origem, Double lat, Double lon, String endereco) {
        // Buscar o sinistro pelo ID
        Sinistro sinistro = sinistroRepository.findById(sinistroId)
                .orElseThrow(() -> new NotFoundException("Sinistro não encontrado"));

        String especialidade = sinistro.getCategoriaProblema();
        List<Oficina> oficinasEspecializadas = oficinaRepository.findByEspecialidadesContaining(especialidade);

        double latOrigem;
        double lonOrigem;

        switch (origem) {
            case CASA:
                latOrigem = sinistro.getCliente().getLatitude();
                lonOrigem = sinistro.getCliente().getLongitude();
                break;
            case SINISTRO:
                latOrigem = sinistro.getLatitude();
                lonOrigem = sinistro.getLongitude();
                break;
            case OUTRO:
                if (endereco != null && !endereco.isEmpty()) {
                    Map<String, Double> coordinates = geocodingService.getCoordinates(endereco);
                    latOrigem = coordinates.get("latitude");
                    lonOrigem = coordinates.get("longitude");
                } else if (lat != null && lon != null) {
                    latOrigem = lat;
                    lonOrigem = lon;
                } else {
                    throw new IllegalArgumentException("Para origem 'OUTRO', forneça um endereço ou latitude e longitude.");
                }
                break;
            default:
                throw new IllegalArgumentException("Origem de busca inválida");
        }

        // Ordenar oficinas pela distância calculada e retornar a lista ordenada
        return oficinasEspecializadas.stream()
                .sorted(Comparator.comparing(oficina ->
                        distancia(latOrigem, lonOrigem, oficina.getLatitude(), oficina.getLongitude())))
                .collect(Collectors.toList());
    }

    // Calcular a distância entre dois pontos geográficos (Fórmula de Haversine)
    private double distancia(double lat1, double lon1, double lat2, double lon2) {
        if (lat2 == 0.0 || lon2 == 0.0) return Double.MAX_VALUE;
        
        final int R = 6371; // Raio da Terra em quilômetros

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public List<Oficina> findAll() {
        return oficinaRepository.findAll();
    }

    // Método para criar uma nova oficina
    public Oficina create(Oficina oficina) {
        if ((oficina.getLatitude() == null || oficina.getLongitude() == null) && (oficina.getEndereco() != null && !oficina.getEndereco().isEmpty())) {
            Map<String, Double> coordinates = geocodingService.getCoordinates(oficina.getEndereco());
            oficina.setLatitude(coordinates.get("latitude"));
            oficina.setLongitude(coordinates.get("longitude"));
        }
        return oficinaRepository.save(oficina);
    }
}
