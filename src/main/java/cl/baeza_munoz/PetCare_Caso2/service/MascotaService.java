package cl.baeza_munoz.PetCare_Caso2.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.baeza_munoz.PetCare_Caso2.dto.MascotaDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.exception.RecursoNoEncontradoException;
import cl.baeza_munoz.PetCare_Caso2.model.Mascota;
import cl.baeza_munoz.PetCare_Caso2.repository.MascotaRepository;

@Service
@Transactional(readOnly = true)
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final ClienteService clienteService;

    public MascotaService(MascotaRepository mascotaRepository, ClienteService clienteService) {
        this.mascotaRepository = mascotaRepository;
        this.clienteService = clienteService;
    }

    
    public List<MascotaDto> listar(Long clienteId, String busqueda) {
        List<Mascota> mascotas = clienteId == null
                ? mascotaRepository.listarConCliente()
                : mascotaRepository.listarPorCliente(clienteId);

        String q = busqueda == null ? "" : busqueda.trim().toLowerCase();
        return mascotas.stream()
                .filter(m -> q.isEmpty()
                        || m.getNombre().toLowerCase().contains(q)
                        || m.getEspecie().toLowerCase().contains(q)
                        || m.getRaza().toLowerCase().contains(q)
                        || m.getCliente().getNombre().toLowerCase().contains(q))
                .map(this::toDto)
                .toList();
    }

    
    public List<MascotaDto> listarPorCliente(Long clienteId) {
        clienteService.buscarEntidad(clienteId);
        return mascotaRepository.listarPorCliente(clienteId).stream().map(this::toDto).toList();
    }

    public MascotaDto obtener(Long id) {
        return toDto(buscarEntidad(id));
    }

    
    public Mascota buscarEntidad(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la mascota con id " + id));
    }

    public long contar() {
        return mascotaRepository.count();
    }

    @Transactional
    public MascotaDto guardar(MascotaDto dto) {
        Mascota mascota = dto.getId() == null ? new Mascota() : buscarEntidad(dto.getId());
        mascota.setCliente(clienteService.buscarEntidad(dto.getClienteId()));
        mascota.setNombre(dto.getNombre().trim());
        mascota.setEspecie(dto.getEspecie().trim());
        mascota.setRaza(dto.getRaza().trim());
        mascota.setFechaNacimiento(dto.getFechaNacimiento());
        mascota.setPeso(dto.getPeso());
        return toDto(mascotaRepository.save(mascota));
    }

    @Transactional
    public void eliminar(Long id) {
        Mascota mascota = buscarEntidad(id);
        long reservas = mascotaRepository.contarReservas(id);
        if (reservas > 0) {
            throw new ConflictoException("No se puede eliminar a " + mascota.getNombre() + ": tiene "
                    + reservas + " reserva(s) en su historial.");
        }
        mascotaRepository.delete(mascota);
    }

    private MascotaDto toDto(Mascota m) {
        MascotaDto dto = new MascotaDto();
        dto.setId(m.getId());
        dto.setClienteId(m.getCliente().getId());
        dto.setClienteNombre(m.getCliente().getNombre());
        dto.setNombre(m.getNombre());
        dto.setEspecie(m.getEspecie());
        dto.setRaza(m.getRaza());
        dto.setFechaNacimiento(m.getFechaNacimiento());
        dto.setPeso(m.getPeso());
        return dto;
    }
}
