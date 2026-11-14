package cl.baeza_munoz.PetCare_Caso2.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.baeza_munoz.PetCare_Caso2.dto.VeterinarioDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.exception.RecursoNoEncontradoException;
import cl.baeza_munoz.PetCare_Caso2.model.Veterinario;
import cl.baeza_munoz.PetCare_Caso2.repository.VeterinarioRepository;

@Service
@Transactional(readOnly = true)
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioService(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    public List<VeterinarioDto> listar() {
        return veterinarioRepository.findAllByOrderByNombreAsc().stream().map(this::toDto).toList();
    }

    public VeterinarioDto obtener(Long id) {
        return toDto(buscarEntidad(id));
    }

    
    public Veterinario buscarEntidad(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el veterinario con id " + id));
    }

    public long contar() {
        return veterinarioRepository.count();
    }

    @Transactional
    public VeterinarioDto guardar(VeterinarioDto dto) {
        Veterinario vet = dto.getId() == null ? new Veterinario() : buscarEntidad(dto.getId());
        vet.setNombre(dto.getNombre().trim());
        return toDto(veterinarioRepository.save(vet));
    }

    @Transactional
    public void eliminar(Long id) {
        Veterinario vet = buscarEntidad(id);
        long reservas = veterinarioRepository.contarReservas(id);
        if (reservas > 0) {
            throw new ConflictoException("No se puede eliminar a " + vet.getNombre() + ": tiene "
                    + reservas + " reserva(s) asociada(s).");
        }
        long horarios = veterinarioRepository.contarHorarios(id);
        if (horarios > 0) {
            throw new ConflictoException("No se puede eliminar a " + vet.getNombre() + ": tiene "
                    + horarios + " hora(s) de atención registrada(s).");
        }
        veterinarioRepository.delete(vet);
    }

    private VeterinarioDto toDto(Veterinario v) {
        VeterinarioDto dto = new VeterinarioDto();
        dto.setId(v.getId());
        dto.setNombre(v.getNombre());
        return dto;
    }
}
