package cl.baeza_munoz.PetCare_Caso2.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import cl.baeza_munoz.PetCare_Caso2.model.EstadoReserva;
import jakarta.validation.constraints.NotNull;

public class ReservaDto {

    private Long id;

    @NotNull(message = "Debes seleccionar una mascota")
    private Long mascotaId;

    private String mascotaNombre;
    private String mascotaEspecie;
    private Long clienteId;
    private String clienteNombre;
    private String clienteCorreo;
    private String clienteTelefono;

    @NotNull(message = "Debes seleccionar un veterinario")
    private Long veterinarioId;

    private String veterinarioNombre;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime hora;

    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    private boolean tieneTratamiento;
    private Long tratamientoId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
    }

    public Long getVeterinarioId() {
        return veterinarioId;
    }

    public void setVeterinarioId(Long veterinarioId) {
        this.veterinarioId = veterinarioId;
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

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public boolean isTieneTratamiento() {
        return tieneTratamiento;
    }

    public void setTieneTratamiento(boolean tieneTratamiento) {
        this.tieneTratamiento = tieneTratamiento;
    }

    public Long getTratamientoId() {
        return tratamientoId;
    }

    public void setTratamientoId(Long tratamientoId) {
        this.tratamientoId = tratamientoId;
    }

    public boolean isPendiente() {
        return estado == EstadoReserva.PENDIENTE;
    }

    public boolean isConfirmada() {
        return estado == EstadoReserva.CONFIRMADA;
    }

    public boolean isCancelada() {
        return estado == EstadoReserva.CANCELADA;
    }

    public boolean isAtendida() {
        return estado == EstadoReserva.ATENDIDA;
    }

    public String getEstadoBadgeClase() {
        if (estado == null) return "tag gr";
        return switch (estado) {
            case CONFIRMADA -> "tag ok";
            case ATENDIDA -> "tag teal";
            case CANCELADA -> "tag er";
            case PENDIENTE -> "tag lemon";
        };
    }
}
