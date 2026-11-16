package cl.baeza_munoz.PetCare_Caso2.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.baeza_munoz.PetCare_Caso2.dto.MedicamentoPrescritoDto;
import cl.baeza_munoz.PetCare_Caso2.dto.TratamientoDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.exception.RecursoNoEncontradoException;
import cl.baeza_munoz.PetCare_Caso2.model.EstadoReserva;
import cl.baeza_munoz.PetCare_Caso2.model.MedicamentoPrescrito;
import cl.baeza_munoz.PetCare_Caso2.model.Reserva;
import cl.baeza_munoz.PetCare_Caso2.model.Tratamiento;
import cl.baeza_munoz.PetCare_Caso2.repository.TratamientoRepository;

@Service
@Transactional(readOnly = true)
public class TratamientoService {

    private final TratamientoRepository tratamientoRepository;
    private final ReservaService reservaService;

    public TratamientoService(TratamientoRepository tratamientoRepository, ReservaService reservaService) {
        this.tratamientoRepository = tratamientoRepository;
        this.reservaService = reservaService;
    }

    public List<TratamientoDto> listar(String busqueda) {
        List<Tratamiento> tratamientos = tratamientoRepository.listarConRelaciones();

        String q = busqueda == null ? "" : busqueda.trim().toLowerCase();

        return tratamientos.stream()
                .filter(t -> q.isEmpty()
                        || t.getReserva().getMascota().getNombre().toLowerCase().contains(q)
                        || t.getReserva().getMascota().getCliente().getNombre().toLowerCase().contains(q)
                        || t.getReserva().getVeterinario().getNombre().toLowerCase().contains(q)
                        || t.getDiagnostico().toLowerCase().contains(q)
                        || t.getMedicamentos().stream().anyMatch(m -> m.getNombre().toLowerCase().contains(q)))
                .map(this::toDto)
                .toList();
    }

    public TratamientoDto obtener(Long id) {
        return toDto(buscarEntidad(id));
    }

    public Tratamiento buscarEntidad(Long id) {
        return tratamientoRepository.buscarPorIdConRelaciones(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el tratamiento con id " + id));
    }

    public long contar() {
        return tratamientoRepository.count();
    }

    @Transactional
    public TratamientoDto guardar(TratamientoDto dto) {
        boolean esNuevo = dto.getId() == null;

        Reserva reserva = reservaService.buscarEntidad(dto.getReservaId());

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ConflictoException("No se puede registrar un tratamiento para una reserva cancelada.");
        }

        if (esNuevo && tratamientoRepository.existsByReservaId(dto.getReservaId())) {
            throw new ConflictoException("Ya existe un tratamiento registrado para esta reserva.");
        }

        Tratamiento tratamiento = esNuevo ? new Tratamiento() : buscarEntidad(dto.getId());

        tratamiento.setReserva(reserva);
        tratamiento.setDiagnostico(dto.getDiagnostico().trim());
        tratamiento.setDescripcion(dto.getDescripcion().trim());

        // Actualizar medicamentos prescritos
        tratamiento.getMedicamentos().clear();
        if (dto.getMedicamentos() != null) {
            for (MedicamentoPrescritoDto medDto : dto.getMedicamentos()) {
                if (medDto.getNombre() != null && !medDto.getNombre().isBlank()) {
                    MedicamentoPrescrito mp = new MedicamentoPrescrito();
                    mp.setNombre(medDto.getNombre().trim());
                    mp.setIndicaciones(medDto.getIndicaciones() != null ? medDto.getIndicaciones().trim() : "");
                    tratamiento.agregarMedicamento(mp);
                }
            }
        }

        // Al registrar tratamiento, la reserva pasa a estar ATENDIDA
        reserva.setEstado(EstadoReserva.ATENDIDA);

        return toDto(tratamientoRepository.save(tratamiento));
    }

    @Transactional
    public void eliminar(Long id) {
        Tratamiento tratamiento = buscarEntidad(id);
        tratamientoRepository.delete(tratamiento);
    }

    private TratamientoDto toDto(Tratamiento t) {
        TratamientoDto dto = new TratamientoDto();
        dto.setId(t.getId());
        dto.setReservaId(t.getReserva().getId());
        dto.setDiagnostico(t.getDiagnostico());
        dto.setDescripcion(t.getDescripcion());

        dto.setMascotaId(t.getReserva().getMascota().getId());
        dto.setMascotaNombre(t.getReserva().getMascota().getNombre());
        dto.setMascotaEspecie(t.getReserva().getMascota().getEspecie());
        dto.setClienteId(t.getReserva().getMascota().getCliente().getId());
        dto.setClienteNombre(t.getReserva().getMascota().getCliente().getNombre());
        dto.setClienteCorreo(t.getReserva().getMascota().getCliente().getCorreo());
        dto.setVeterinarioNombre(t.getReserva().getVeterinario().getNombre());
        dto.setFecha(t.getReserva().getFecha());
        dto.setHora(t.getReserva().getHora());

        List<MedicamentoPrescritoDto> meds = new ArrayList<>();
        if (t.getMedicamentos() != null) {
            for (MedicamentoPrescrito mp : t.getMedicamentos()) {
                MedicamentoPrescritoDto medDto = new MedicamentoPrescritoDto();
                medDto.setId(mp.getId());
                medDto.setNombre(mp.getNombre());
                medDto.setIndicaciones(mp.getIndicaciones());
                meds.add(medDto);
            }
        }
        dto.setMedicamentos(meds);

        return dto;
    }
}
