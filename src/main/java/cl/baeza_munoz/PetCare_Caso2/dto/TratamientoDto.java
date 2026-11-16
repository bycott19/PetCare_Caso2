package cl.baeza_munoz.PetCare_Caso2.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TratamientoDto {

    private Long id;

    @NotNull(message = "Debes seleccionar una reserva")
    private Long reservaId;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    @NotBlank(message = "La descripción del tratamiento es obligatoria")
    private String descripcion;

    @Valid
    private List<MedicamentoPrescritoDto> medicamentos = new ArrayList<>();

    // Campos informativos de la reserva
    private Long mascotaId;
    private String mascotaNombre;
    private String mascotaEspecie;
    private Long clienteId;
    private String clienteNombre;
    private String clienteCorreo;
    private String veterinarioNombre;
    private LocalDate fecha;
    private LocalTime hora;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<MedicamentoPrescritoDto> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<MedicamentoPrescritoDto> medicamentos) {
        this.medicamentos = medicamentos;
    }

    public Long getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(Long mascotaId) {
        this.mascotaId = mascotaId;
    }

    public String getMascotaNombre() {
        return mascotaNombre;
    }

    public void setMascotaNombre(String mascotaNombre) {
        this.mascotaNombre = mascotaNombre;
    }

    public String getMascotaEspecie() {
        return mascotaEspecie;
    }

    public void setMascotaEspecie(String mascotaEspecie) {
        this.mascotaEspecie = mascotaEspecie;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteCorreo() {
        return clienteCorreo;
    }

    public void setClienteCorreo(String clienteCorreo) {
        this.clienteCorreo = clienteCorreo;
    }

    public String getVeterinarioNombre() {
        return veterinarioNombre;
    }

    public void setVeterinarioNombre(String veterinarioNombre) {
        this.veterinarioNombre = veterinarioNombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
}
