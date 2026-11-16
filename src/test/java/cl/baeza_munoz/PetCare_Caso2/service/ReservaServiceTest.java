package cl.baeza_munoz.PetCare_Caso2.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.baeza_munoz.PetCare_Caso2.dto.ReservaDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.model.Cliente;
import cl.baeza_munoz.PetCare_Caso2.model.EstadoReserva;
import cl.baeza_munoz.PetCare_Caso2.model.Mascota;
import cl.baeza_munoz.PetCare_Caso2.model.Reserva;
import cl.baeza_munoz.PetCare_Caso2.model.Veterinario;
import cl.baeza_munoz.PetCare_Caso2.repository.ReservaRepository;
import cl.baeza_munoz.PetCare_Caso2.repository.TratamientoRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private MascotaService mascotaService;

    @Mock
    private VeterinarioService veterinarioService;

    @Mock
    private TratamientoRepository tratamientoRepository;

    @Mock
    private PetCareApiClient petCareApiClient;

    private ReservaService reservaService;

    private Cliente cliente;
    private Mascota mascota;
    private Veterinario veterinario;

    @BeforeEach
    void setUp() {
        reservaService = new ReservaService(
                reservaRepository,
                mascotaService,
                veterinarioService,
                tratamientoRepository,
                petCareApiClient
        );

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Camila Rojas");
        cliente.setCorreo("camila@mail.cl");
        cliente.setTelefono("912345678");

        mascota = new Mascota();
        mascota.setId(2L);
        mascota.setNombre("Luna");
        mascota.setEspecie("Perro");
        mascota.setCliente(cliente);

        veterinario = new Veterinario();
        veterinario.setId(3L);
        veterinario.setNombre("Dra. Valentina Pérez");
    }

    @Test
    void guardar_nuevaReservaSinConflicto_guardaConExito() {
        LocalDate fecha = LocalDate.of(2026, 11, 20);
        LocalTime hora = LocalTime.of(10, 30);

        ReservaDto dto = new ReservaDto();
        dto.setMascotaId(2L);
        dto.setVeterinarioId(3L);
        dto.setFecha(fecha);
        dto.setHora(hora);

        when(reservaRepository.existeConflictoCupo(fecha, hora, null)).thenReturn(false);
        when(mascotaService.buscarEntidad(2L)).thenReturn(mascota);
        when(veterinarioService.buscarEntidad(3L)).thenReturn(veterinario);

        Reserva guardada = new Reserva();
        guardada.setId(10L);
        guardada.setMascota(mascota);
        guardada.setVeterinario(veterinario);
        guardada.setFecha(fecha);
        guardada.setHora(hora);
        guardada.setEstado(EstadoReserva.PENDIENTE);

        when(reservaRepository.save(any(Reserva.class))).thenReturn(guardada);

        ReservaDto resultado = reservaService.guardar(dto);

        assertNotNull(resultado);
        assertEquals(EstadoReserva.PENDIENTE, resultado.getEstado());
        assertEquals("Luna", resultado.getMascotaNombre());
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void guardar_conflictoFechaHora_lanzaConflictoException() {
        LocalDate fecha = LocalDate.of(2026, 11, 20);
        LocalTime hora = LocalTime.of(10, 30);

        ReservaDto dto = new ReservaDto();
        dto.setMascotaId(2L);
        dto.setVeterinarioId(3L);
        dto.setFecha(fecha);
        dto.setHora(hora);

        when(reservaRepository.existeConflictoCupo(fecha, hora, null)).thenReturn(true);

        ConflictoException ex = assertThrows(ConflictoException.class, () -> reservaService.guardar(dto));
        assert(ex.getMessage().contains("Ya existe una reserva activa"));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void confirmar_reservaPendiente_cambiaAConfirmadaYEnviaNotificacion() {
        Reserva reserva = new Reserva();
        reserva.setId(5L);
        reserva.setMascota(mascota);
        reserva.setVeterinario(veterinario);
        reserva.setFecha(LocalDate.of(2026, 11, 20));
        reserva.setHora(LocalTime.of(10, 30));
        reserva.setEstado(EstadoReserva.PENDIENTE);

        when(reservaRepository.buscarPorIdConRelaciones(5L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.existeConflictoCupo(reserva.getFecha(), reserva.getHora(), 5L)).thenReturn(false);

        reservaService.confirmar(5L);

        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        verify(reservaRepository).save(reserva);
        verify(petCareApiClient).enviarNotificacion(argThat(req ->
                "CONFIRMACION".equals(req.getTipo())
                        && "camila@mail.cl".equals(req.getDestinatario())
                        && "Luna".equals(req.getNombreMascota())
        ));
    }

    @Test
    void cancelar_reserva_cambiaACanceladaYEnviaNotificacion() {
        Reserva reserva = new Reserva();
        reserva.setId(5L);
        reserva.setMascota(mascota);
        reserva.setVeterinario(veterinario);
        reserva.setFecha(LocalDate.of(2026, 11, 20));
        reserva.setHora(LocalTime.of(10, 30));
        reserva.setEstado(EstadoReserva.CONFIRMADA);

        when(reservaRepository.buscarPorIdConRelaciones(5L)).thenReturn(Optional.of(reserva));

        reservaService.cancelar(5L);

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        verify(reservaRepository).save(reserva);
        verify(petCareApiClient).enviarNotificacion(argThat(req ->
                "CANCELACION".equals(req.getTipo())
                        && "camila@mail.cl".equals(req.getDestinatario())
        ));
    }

    @Test
    void cancelar_reservaAtendida_lanzaConflictoException() {
        Reserva reserva = new Reserva();
        reserva.setId(5L);
        reserva.setEstado(EstadoReserva.ATENDIDA);

        when(reservaRepository.buscarPorIdConRelaciones(5L)).thenReturn(Optional.of(reserva));

        assertThrows(ConflictoException.class, () -> reservaService.cancelar(5L));
        verify(reservaRepository, never()).save(any());
        verify(petCareApiClient, never()).enviarNotificacion(any());
    }
}
