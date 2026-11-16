package cl.baeza_munoz.PetCare_Caso2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MedicamentoPrescritoDto {

    private Long id;

    @NotBlank(message = "El nombre del medicamento es obligatorio")
    @Size(max = 160, message = "Máximo 160 caracteres")
    private String nombre;

    @NotBlank(message = "Las indicaciones son obligatorias")
    private String indicaciones;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }
}
