package cl.baeza_munoz.PetCare_Caso2.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import cl.baeza_munoz.PetCare_Caso2.dto.MedicamentoApiDto;
import cl.baeza_munoz.PetCare_Caso2.dto.MedicamentoConsultaResponseDto;
import cl.baeza_munoz.PetCare_Caso2.dto.NotificacionRequestDto;

@Component
public class PetCareApiClient {

    private static final Logger log = LoggerFactory.getLogger(PetCareApiClient.class);

    private final RestClient restClient;
    private final String notificacionesApiKey;

    public PetCareApiClient(
            @Value("${petcare.api.base-url:${petcare.api.url:http://localhost:3000}}") String apiUrl,
            @Value("${NOTIFICACIONES_API_KEY:${petcare.api.notificaciones-api-key:}}") String notificacionesApiKey) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .requestFactory(factory)
                .build();
        this.notificacionesApiKey = notificacionesApiKey != null ? notificacionesApiKey.trim() : "";
    }

    public boolean enviarNotificacion(NotificacionRequestDto peticion) {
        try {
            var request = restClient.post()
                    .uri("/api/notificaciones")
                    .contentType(MediaType.APPLICATION_JSON);

            if (!notificacionesApiKey.isEmpty()) {
                request.header("Authorization", "Bearer " + notificacionesApiKey);
            }

            request.body(peticion)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Notificación enviada con éxito a API de Node (tipo: {}, destinatario: {})",
                    peticion.getTipo(), peticion.getDestinatario());
            return true;
        } catch (Exception e) {
            log.warn("No se pudo enviar la notificación a la API de Node (tipo: {}, destinatario: {}): {}",
                    peticion.getTipo(), peticion.getDestinatario(), e.getMessage());
            return false;
        }
    }

    public List<MedicamentoApiDto> buscarMedicamentos(String nombre, String especie) {
        try {
            MedicamentoConsultaResponseDto resp = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/medicamentos")
                            .queryParam("nombre", nombre != null ? nombre : "")
                            .queryParam("especie", especie != null ? especie : "")
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(MedicamentoConsultaResponseDto.class);

            return resp != null && resp.getResultados() != null ? resp.getResultados() : Collections.emptyList();
        } catch (Exception e) {
            log.warn("No se pudo consultar medicamentos en la API de Node: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public MedicamentoApiDto obtenerMedicamento(Long id) {
        try {
            return restClient.get()
                    .uri("/api/medicamentos/{id}", id)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(MedicamentoApiDto.class);
        } catch (Exception e) {
            log.warn("No se pudo obtener el medicamento con id {} en la API de Node: {}", id, e.getMessage());
            return null;
        }
    }
}
