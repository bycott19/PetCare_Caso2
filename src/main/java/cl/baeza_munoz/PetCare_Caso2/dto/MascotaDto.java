package cl.baeza_munoz.PetCare_Caso2.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public class MascotaDto {

    private Long id;

    @NotNull(message = "Selecciona el dueño de la mascota")
    private Long clienteId;

    
    private String clienteNombre;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "Máximo 80 caracteres")
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String especie;

    @NotBlank(message = "La raza es obligatoria (puedes escribir Mestiza)")
    @Size(max = 80, message = "Máximo 80 caracteres")
    private String raza;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.001", message = "El peso debe ser mayor a 0")
    @DecimalMax(value = "9999.999", message = "Peso fuera de rango")
    private BigDecimal peso;

    
    public String getEdad() {
        if (fechaNacimiento == null) return "-";
        Period p = Period.between(fechaNacimiento, LocalDate.now());
        if (p.getYears() > 0) {
            String txt = p.getYears() + (p.getYears() == 1 ? " año" : " años");
            if (p.getMonths() > 0) txt += " " + p.getMonths() + (p.getMonths() == 1 ? " mes" : " meses");
            return txt;
        }
        if (p.getMonths() > 0) return p.getMonths() + (p.getMonths() == 1 ? " mes" : " meses");
        return p.getDays() + (p.getDays() == 1 ? " día" : " días");
    }

    
    public String getTipo() {
        if (especie == null) return "otro";
        String e = especie.trim().toLowerCase();
        if (e.startsWith("perr") || e.startsWith("can")) return "perro";
        if (e.startsWith("gat") || e.startsWith("fel")) return "gato";
        return "otro";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }
}
