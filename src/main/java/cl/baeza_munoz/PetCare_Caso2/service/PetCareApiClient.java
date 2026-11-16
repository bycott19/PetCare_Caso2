package cl.baeza_munoz.PetCare_Caso2.service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import cl.baeza_munoz.PetCare_Caso2.dto.MedicamentoApiDto;
import cl.baeza_munoz.PetCare_Caso2.dto.MedicamentoConsultaResponseDto;
import cl.baeza_munoz.PetCare_Caso2.dto.NotificacionRequestDto;

@Component
public class PetCareApiClient {

    private static final Logger log = LoggerFactory.getLogger(PetCareApiClient.class);

    private final RestClient restClient;

    public PetCareApiClient(@Value("${petcare.api.url:http://localhost:3000}") String apiUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    public boolean enviarNotificacion(NotificacionRequestDto peticion) {
        try {
            restClient.post()
                    .uri("/api/notificaciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(peticion)
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
