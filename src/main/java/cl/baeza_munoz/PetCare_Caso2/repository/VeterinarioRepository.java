package cl.baeza_munoz.PetCare_Caso2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.baeza_munoz.PetCare_Caso2.model.Veterinario;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    List<Veterinario> findAllByOrderByNombreAsc();

    
    @Query(value = "select count(*) from reserva where veterinario_id = :id", nativeQuery = true)
    long contarReservas(@Param("id") Long id);

    
    @Query(value = "select count(*) from horario_veterinario where veterinario_id = :id", nativeQuery = true)
    long contarHorarios(@Param("id") Long id);
}
