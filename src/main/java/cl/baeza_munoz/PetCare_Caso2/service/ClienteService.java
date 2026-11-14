package cl.baeza_munoz.PetCare_Caso2.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.baeza_munoz.PetCare_Caso2.dto.ClienteDto;
import cl.baeza_munoz.PetCare_Caso2.exception.ConflictoException;
import cl.baeza_munoz.PetCare_Caso2.exception.RecursoNoEncontradoException;
import cl.baeza_munoz.PetCare_Caso2.model.Cliente;
import cl.baeza_munoz.PetCare_Caso2.repository.ClienteRepository;
import cl.baeza_munoz.PetCare_Caso2.repository.MascotaRepository;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;

    public ClienteService(ClienteRepository clienteRepository, MascotaRepository mascotaRepository) {
        this.clienteRepository = clienteRepository;
        this.mascotaRepository = mascotaRepository;
    }

    public List<ClienteDto> listar(String busqueda) {
        List<Cliente> clientes = (busqueda == null || busqueda.isBlank())
                ? clienteRepository.findAllByOrderByNombreAsc()
                : clienteRepository.buscar(busqueda.trim());
        return clientes.stream().map(this::toDto).toList();
    }

    public ClienteDto obtener(Long id) {
        return toDto(buscarEntidad(id));
    }

    
    public Cliente buscarEntidad(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cliente con id " + id));
    }

    public long contar() {
        return clienteRepository.count();
    }

    @Transactional
    public ClienteDto guardar(ClienteDto dto) {
        String rut = normalizarRut(dto.getRut());
        if (rut == null) {
            throw new ConflictoException("El RUT ingresado no es válido (revisa el dígito verificador)");
        }
        boolean duplicado = dto.getId() == null
                ? clienteRepository.existsByRut(rut)
                : clienteRepository.existsByRutAndIdNot(rut, dto.getId());
        if (duplicado) {
            throw new ConflictoException("Ya existe un cliente registrado con el RUT " + rut);
        }

        Cliente cliente = dto.getId() == null ? new Cliente() : buscarEntidad(dto.getId());
        cliente.setRut(rut);
        cliente.setNombre(dto.getNombre().trim());
        cliente.setCorreo(dto.getCorreo().trim().toLowerCase());
        cliente.setTelefono(dto.getTelefono().trim());
        return toDto(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = buscarEntidad(id);
        long mascotas = mascotaRepository.countByClienteId(id);
        if (mascotas > 0) {
            throw new ConflictoException("No se puede eliminar a " + cliente.getNombre() + ": tiene "
                    + mascotas + " mascota(s) registrada(s). Elimínalas primero.");
        }
        clienteRepository.delete(cliente);
    }

    private ClienteDto toDto(Cliente c) {
        ClienteDto dto = new ClienteDto();
        dto.setId(c.getId());
        dto.setRut(c.getRut());
        dto.setNombre(c.getNombre());
        dto.setCorreo(c.getCorreo());
        dto.setTelefono(c.getTelefono());
        dto.setCantidadMascotas(c.getMascotas().size());
        return dto;
    }

    
    public static String normalizarRut(String rut) {
        if (rut == null) return null;
        String limpio = rut.replace(".", "").replace("-", "").replace(" ", "").toUpperCase();
        if (!limpio.matches("^[0-9]{7,8}[0-9K]$")) return null;

        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);

        int suma = 0;
        int factor = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += (cuerpo.charAt(i) - '0') * factor;
            factor = factor == 7 ? 2 : factor + 1;
        }
        int resto = 11 - (suma % 11);
        char esperado = resto == 11 ? '0' : resto == 10 ? 'K' : (char) ('0' + resto);
        return dv == esperado ? cuerpo + "-" + dv : null;
    }
}
