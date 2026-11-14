package cl.baeza_munoz.PetCare_Caso2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.baeza_munoz.PetCare_Caso2.model.Mascota;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    @Query("select m from Mascota m join fetch m.cliente order by m.nombre")
    List<Mascota> listarConCliente();

    @Query("select m from Mascota m join fetch m.cliente c where c.id = :clienteId order by m.nombre")
    List<Mascota> listarPorCliente(@Param("clienteId") Long clienteId);

    long countByClienteId(Long clienteId);

    
    @Query(value = "select count(*) from reserva where mascota_id = :id", nativeQuery = true)
    long contarReservas(@Param("id") Long id);
}
