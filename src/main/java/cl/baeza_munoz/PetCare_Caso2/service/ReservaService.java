package cl.baeza_munoz.PetCare_Caso2.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.baeza_munoz.PetCare_Caso2.dto.NotificacionRequestDto;
import cl.baeza_munoz.PetCare_Caso2.dto.ReservaDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.exception.RecursoNoEncontradoException;
import cl.baeza_munoz.PetCare_Caso2.model.EstadoReserva;
import cl.baeza_munoz.PetCare_Caso2.model.Mascota;
import cl.baeza_munoz.PetCare_Caso2.model.Reserva;
import cl.baeza_munoz.PetCare_Caso2.model.Tratamiento;
import cl.baeza_munoz.PetCare_Caso2.model.Veterinario;
import cl.baeza_munoz.PetCare_Caso2.repository.ReservaRepository;
import cl.baeza_munoz.PetCare_Caso2.repository.TratamientoRepository;

@Service
@Transactional(readOnly = true)
public class ReservaService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ReservaRepository reservaRepository;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final TratamientoRepository tratamientoRepository;
    private final PetCareApiClient petCareApiClient;

    public ReservaService(ReservaRepository reservaRepository,
                          MascotaService mascotaService,
                          VeterinarioService veterinarioService,
                          TratamientoRepository tratamientoRepository,
                          PetCareApiClient petCareApiClient) {
        this.reservaRepository = reservaRepository;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
        this.tratamientoRepository = tratamientoRepository;
        this.petCareApiClient = petCareApiClient;
    }

    public List<ReservaDto> listar(Long veterinarioId, EstadoReserva estado, LocalDate fecha, String busqueda) {
        List<Reserva> reservas = reservaRepository.listarConRelaciones();

        String q = busqueda == null ? "" : busqueda.trim().toLowerCase();

        return reservas.stream()
                .filter(r -> veterinarioId == null || r.getVeterinario().getId().equals(veterinarioId))
                .filter(r -> estado == null || r.getEstado() == estado)
                .filter(r -> fecha == null || r.getFecha().equals(fecha))
                .filter(r -> q.isEmpty()
                        || r.getMascota().getNombre().toLowerCase().contains(q)
                        || r.getMascota().getCliente().getNombre().toLowerCase().contains(q)
                        || r.getVeterinario().getNombre().toLowerCase().contains(q))
                .map(this::toDto)
                .toList();
    }

    public List<ReservaDto> listarDisponiblesParaTratamiento() {
        return reservaRepository.listarDisponiblesParaTratamiento().stream()
                .map(this::toDto)
                .toList();
    }

    public ReservaDto obtener(Long id) {
        return toDto(buscarEntidad(id));
    }

    public Reserva buscarEntidad(Long id) {
        return reservaRepository.buscarPorIdConRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la reserva con id " + id));
    }

    public long contar() {
        return reservaRepository.count();
    }

    public long contarPorEstado(EstadoReserva estado) {
        return reservaRepository.countByEstado(estado);
    }

    @Transactional
    public ReservaDto guardar(ReservaDto dto) {
        // Validar conflicto de reserva el mismo día y hora (excepto si está CANCELADA)
        if (dto.getEstado() != EstadoReserva.CANCELADA) {
            boolean conflicto = reservaRepository.existeConflictoCupo(dto.getFecha(), dto.getHora(), dto.getId());
            if (conflicto) {
                throw new ConflictoException("Ya existe una reserva activa para el día "
                        + dto.getFecha() + " a las " + dto.getHora().format(TIME_FORMATTER) + ".");
            }
        }

        Mascota mascota = mascotaService.buscarEntidad(dto.getMascotaId());
        Veterinario vet = veterinarioService.buscarEntidad(dto.getVeterinarioId());

        Reserva reserva;
        boolean esNueva = dto.getId() == null;
        EstadoReserva estadoAnterior = null;

        if (esNueva) {
            reserva = new Reserva();
            reserva.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoReserva.PENDIENTE);
        } else {
            reserva = buscarEntidad(dto.getId());
            estadoAnterior = reserva.getEstado();
            if (dto.getEstado() != null) {
                reserva.setEstado(dto.getEstado());
            }
        }

        reserva.setMascota(mascota);
        reserva.setVeterinario(vet);
        reserva.setFecha(dto.getFecha());
        reserva.setHora(dto.getHora());

        Reserva guardada = reservaRepository.save(reserva);

        // Si en la modificación el estado cambió a CONFIRMADA o CANCELADA, notificar a la API
        if (!esNueva && estadoAnterior != null && estadoAnterior != guardada.getEstado()) {
            if (guardada.getEstado() == EstadoReserva.CONFIRMADA) {
                notificarApi(guardada, "CONFIRMACION");
            } else if (guardada.getEstado() == EstadoReserva.CANCELADA) {
                notificarApi(guardada, "CANCELACION");
            }
        }

        return toDto(guardada);
    }

    @Transactional
    public void confirmar(Long id) {
        Reserva reserva = buscarEntidad(id);
        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            return;
        }

        // Si estaba cancelada y se confirma, validar que no haya conflicto en esa fecha/hora
        boolean conflicto = reservaRepository.existeConflictoCupo(reserva.getFecha(), reserva.getHora(), reserva.getId());
        if (conflicto) {
            throw new ConflictoException("No se puede confirmar: el cupo del "
                    + reserva.getFecha() + " a las " + reserva.getHora().format(TIME_FORMATTER) + " ya fue tomado.");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reservaRepository.save(reserva);

        notificarApi(reserva, "CONFIRMACION");
    }

    @Transactional
    public void cancelar(Long id) {
        Reserva reserva = buscarEntidad(id);
        if (reserva.getEstado() == EstadoReserva.ATENDIDA) {
            throw new ConflictoException("No se puede cancelar una reserva que ya fue atendida.");
        }
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            return;
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        notificarApi(reserva, "CANCELACION");
    }

    @Transactional
    public void marcarAtendida(Long id) {
        Reserva reserva = buscarEntidad(id);
        reserva.setEstado(EstadoReserva.ATENDIDA);
        reservaRepository.save(reserva);
    }

    @Transactional
    public void eliminar(Long id) {
        Reserva reserva = buscarEntidad(id);
        if (tratamientoRepository.existsByReservaId(id)) {
            throw new ConflictoException("No se puede eliminar la reserva: tiene un tratamiento asociado.");
        }
        reservaRepository.delete(reserva);
    }

    private void notificarApi(Reserva reserva, String tipo) {
        String correo = reserva.getMascota().getCliente().getCorreo();
        if (correo == null || correo.isBlank()) {
            return;
        }

        NotificacionRequestDto req = new NotificacionRequestDto(
                tipo,
                reserva.getId(),
                correo,
                reserva.getMascota().getCliente().getNombre(),
                reserva.getMascota().getNombre(),
                reserva.getVeterinario().getNombre(),
                reserva.getFecha().toString(),
                reserva.getHora().format(TIME_FORMATTER)
        );

        petCareApiClient.enviarNotificacion(req);
    }

    public ReservaDto toDto(Reserva r) {
        ReservaDto dto = new ReservaDto();
        dto.setId(r.getId());
        dto.setMascotaId(r.getMascota().getId());
        dto.setMascotaNombre(r.getMascota().getNombre());
        dto.setMascotaEspecie(r.getMascota().getEspecie());
        dto.setClienteId(r.getMascota().getCliente().getId());
        dto.setClienteNombre(r.getMascota().getCliente().getNombre());
        dto.setClienteCorreo(r.getMascota().getCliente().getCorreo());
        dto.setClienteTelefono(r.getMascota().getCliente().getTelefono());
        dto.setVeterinarioId(r.getVeterinario().getId());
        dto.setVeterinarioNombre(r.getVeterinario().getNombre());
        dto.setFecha(r.getFecha());
        dto.setHora(r.getHora());
        dto.setEstado(r.getEstado());

        tratamientoRepository.findByReservaId(r.getId()).ifPresent(t -> {
            dto.setTieneTratamiento(true);
            dto.setTratamientoId(t.getId());
        });

        return dto;
    }
}
