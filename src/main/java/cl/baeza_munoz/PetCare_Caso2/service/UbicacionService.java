package cl.baeza_munoz.PetCare_Caso2.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import cl.baeza_munoz.PetCare_Caso2.dto.UbicacionDto;

@Service
public class UbicacionService {

    private final RestClient api;

    public UbicacionService(@Value("${petcare.api.base-url}") String apiBaseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        api = RestClient.builder()
                .baseUrl(apiBaseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public UbicacionDto obtener() {
        UbicacionDto ubicacion = api.get()
                .uri("/api/ubicacion")
                .retrieve()
                .body(UbicacionDto.class);
        if (ubicacion == null || ubicacion.mapa() == null
                || ubicacion.mapa().embedUrl() == null || ubicacion.mapa().embedUrl().isBlank()) {
            throw new RestClientException("La API de PetCare devolvió una ubicación incompleta");
        }
        return ubicacion;
    }
}
