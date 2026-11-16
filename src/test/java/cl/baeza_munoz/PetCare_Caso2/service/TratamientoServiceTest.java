package cl.baeza_munoz.PetCare_Caso2.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.baeza_munoz.PetCare_Caso2.dto.MedicamentoPrescritoDto;
import cl.baeza_munoz.PetCare_Caso2.dto.TratamientoDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.model.Cliente;
import cl.baeza_munoz.PetCare_Caso2.model.EstadoReserva;
import cl.baeza_munoz.PetCare_Caso2.model.Mascota;
import cl.baeza_munoz.PetCare_Caso2.model.Reserva;
import cl.baeza_munoz.PetCare_Caso2.model.Tratamiento;
import cl.baeza_munoz.PetCare_Caso2.model.Veterinario;
import cl.baeza_munoz.PetCare_Caso2.repository.TratamientoRepository;

@ExtendWith(MockitoExtension.class)
class TratamientoServiceTest {

    @Mock
    private TratamientoRepository tratamientoRepository;

    @Mock
    private ReservaService reservaService;

    private TratamientoService tratamientoService;

    private Reserva reserva;

    @BeforeEach
    void setUp() {
        tratamientoService = new TratamientoService(tratamientoRepository, reservaService);

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Camila");
        cliente.setCorreo("camila@mail.cl");

        Mascota mascota = new Mascota();
        mascota.setId(2L);
        mascota.setNombre("Luna");
        mascota.setEspecie("Perro");
        mascota.setCliente(cliente);

        Veterinario vet = new Veterinario();
        vet.setId(3L);
        vet.setNombre("Dra. Valentina Pérez");

        reserva = new Reserva();
        reserva.setId(10L);
        reserva.setMascota(mascota);
        reserva.setVeterinario(vet);
        reserva.setFecha(LocalDate.of(2026, 11, 20));
        reserva.setHora(LocalTime.of(10, 30));
        reserva.setEstado(EstadoReserva.CONFIRMADA);
    }

    @Test
    void guardar_nuevoTratamiento_guardaYMarcaReservaAtendida() {
        TratamientoDto dto = new TratamientoDto();
        dto.setReservaId(10L);
        dto.setDiagnostico("Otitis externa");
        dto.setDescripcion("Limpieza de conducto y antibiótico tópico");

        MedicamentoPrescritoDto medDto = new MedicamentoPrescritoDto();
        medDto.setNombre("Amoxicilina con ácido clavulánico");
        medDto.setIndicaciones("1 comprimido cada 12 horas por 7 días");
        dto.setMedicamentos(List.of(medDto));

        when(reservaService.buscarEntidad(10L)).thenReturn(reserva);
        when(tratamientoRepository.existsByReservaId(10L)).thenReturn(false);

        when(tratamientoRepository.save(any(Tratamiento.class))).thenAnswer(invocation -> {
            Tratamiento t = invocation.getArgument(0);
            t.setId(100L);
            return t;
        });

        TratamientoDto resultado = tratamientoService.guardar(dto);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("Otitis externa", resultado.getDiagnostico());
        assertEquals(1, resultado.getMedicamentos().size());
        assertEquals(EstadoReserva.ATENDIDA, reserva.getEstado());
        verify(tratamientoRepository).save(any(Tratamiento.class));
    }

    @Test
    void guardar_reservaYaConTratamiento_lanzaConflictoException() {
        TratamientoDto dto = new TratamientoDto();
        dto.setReservaId(10L);
        dto.setDiagnostico("Diagnóstico");
        dto.setDescripcion("Descripción");

        when(reservaService.buscarEntidad(10L)).thenReturn(reserva);
        when(tratamientoRepository.existsByReservaId(10L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> tratamientoService.guardar(dto));
        verify(tratamientoRepository, never()).save(any());
    }

    @Test
    void guardar_reservaCancelada_lanzaConflictoException() {
        reserva.setEstado(EstadoReserva.CANCELADA);

        TratamientoDto dto = new TratamientoDto();
        dto.setReservaId(10L);
        dto.setDiagnostico("Diagnóstico");
        dto.setDescripcion("Descripción");

        when(reservaService.buscarEntidad(10L)).thenReturn(reserva);

        assertThrows(ConflictoException.class, () -> tratamientoService.guardar(dto));
        verify(tratamientoRepository, never()).save(any());
    }
}
