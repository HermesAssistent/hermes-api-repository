package com.hermes.hermes.service;

import com.hermes.hermes.domain.model.localizacao.Localizacao;
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
        Sinistro sinistro = buscarSinistroPorId(sinistroId);
        String especialidade = sinistro.getCategoriaProblema();
        List<Oficina> oficinasEspecializadas = oficinaRepository.findByEspecialidadesContaining(especialidade);

        double[] coordenadasOrigem = determinarCoordenadasOrigem(origem, sinistro, lat, lon, endereco);
        double latOrigem = coordenadasOrigem[0];
        double lonOrigem = coordenadasOrigem[1];

        return ordenarOficinasPorDistancia(oficinasEspecializadas, latOrigem, lonOrigem);
    }

    private Sinistro buscarSinistroPorId(Long sinistroId) {
        return sinistroRepository.findById(sinistroId)
                .orElseThrow(() -> new NotFoundException("Sinistro não encontrado"));
    }

    private double[] determinarCoordenadasOrigem(OrigemBusca origem, Sinistro sinistro, Double lat, Double lon, String endereco) {
        switch (origem) {
            case CASA:
                return obterCoordenadasCliente(sinistro);
            case SINISTRO:
                return new double[]{sinistro.getLocalizacao().getLatitude(), sinistro.getLocalizacao().getLongitude()};
            case OUTRO:
                return obterCoordenadasOutro(lat, lon, endereco);
            default:
                throw new IllegalArgumentException("Origem de busca inválida");
        }
    }

    private double[] obterCoordenadasCliente(Sinistro sinistro) {
        if (sinistro.getCliente() != null && sinistro.getCliente().getLocalizacao() != null) {
            Localizacao localizacaoCliente = sinistro.getCliente().getLocalizacao();
            if (localizacaoCliente.getEndereco() != null && !localizacaoCliente.getEndereco().isEmpty()) {
                return converterEnderecoParaCoordenadas(localizacaoCliente.getEndereco());
            } else if (localizacaoCliente.getCep() != null && !localizacaoCliente.getCep().isEmpty()) {
                return converterEnderecoParaCoordenadas(localizacaoCliente.getCep());
            }
        }
        throw new IllegalArgumentException("Cliente ou localização do cliente não disponível para origem 'CASA'.");
    }

    private double[] obterCoordenadasOutro(Double lat, Double lon, String endereco) {
        if (endereco != null && !endereco.isEmpty()) {
            return converterEnderecoParaCoordenadas(endereco);
        } else if (lat != null && lon != null) {
            return new double[]{lat, lon};
        }
        throw new IllegalArgumentException("Para origem 'OUTRO', forneça um endereço ou latitude e longitude.");
    }

    
    private double[] converterEnderecoParaCoordenadas(String enderecoOuCep) {
        Localizacao localizacao = geocodingService.getCoordinates(enderecoOuCep);
        if (localizacao == null || localizacao.getLatitude() == 0.0 || localizacao.getLongitude() == 0.0) {
            throw new IllegalArgumentException("Não foi possível obter as coordenadas para o endereço ou CEP fornecido.");
        }
        return new double[]{localizacao.getLatitude(), localizacao.getLongitude()};
    }

    private List<Oficina> ordenarOficinasPorDistancia(List<Oficina> oficinas, double latOrigem, double lonOrigem) {
        return oficinas.stream()
                .sorted(Comparator.comparing(oficina ->
                        distancia(latOrigem, lonOrigem, 
                                  oficina.getLocalizacao().getLatitude(), 
                                  oficina.getLocalizacao().getLongitude())))
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
        if (oficina == null) {
            throw new IllegalArgumentException("A oficina não pode ser nula.");
        }

        Localizacao localizacao = oficina.getLocalizacao();
        if (localizacao == null || (localizacao.getEndereco() == null || localizacao.getEndereco().isEmpty())) {
            throw new IllegalArgumentException("O endereço da oficina deve ser fornecido.");
        }

        // Obtendo as coordenadas com base no endereço
        Localizacao coordenadas = geocodingService.getCoordinates(localizacao.getEndereco());
        localizacao.setLatitude(coordenadas.getLatitude());
        localizacao.setLongitude(coordenadas.getLongitude());

        return oficinaRepository.save(oficina);
    }
}