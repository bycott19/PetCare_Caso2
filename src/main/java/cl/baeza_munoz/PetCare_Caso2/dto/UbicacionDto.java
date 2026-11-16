package cl.baeza_munoz.PetCare_Caso2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UbicacionDto(String nombre, String direccion, String comuna, String region,
                           String telefono, String horario, Mapa mapa) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Mapa(String embedUrl, String comoLlegarUrl) {
    }
}
