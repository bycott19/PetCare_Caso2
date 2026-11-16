package cl.baeza_munoz.PetCare_Caso2.dto;

public class NotificacionRequestDto {

    private String tipo;
    private Long reservaId;
    private String destinatario;
    private String nombreCliente;
    private String nombreMascota;
    private String nombreVeterinario;
    private String fecha; // formato YYYY-MM-DD
    private String hora;  // formato HH:mm

    public NotificacionRequestDto() {}

    public NotificacionRequestDto(String tipo, Long reservaId, String destinatario,
                                  String nombreCliente, String nombreMascota,
                                  String nombreVeterinario, String fecha, String hora) {
        this.tipo = tipo;
        this.reservaId = reservaId;
        this.destinatario = destinatario;
        this.nombreCliente = nombreCliente;
        this.nombreMascota = nombreMascota;
        this.nombreVeterinario = nombreVeterinario;
        this.fecha = fecha;
        this.hora = hora;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    public String getNombreVeterinario() { return nombreVeterinario; }
    public void setNombreVeterinario(String nombreVeterinario) { this.nombreVeterinario = nombreVeterinario; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }
}
