package cl.baeza_munoz.PetCare_Caso2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.baeza_munoz.PetCare_Caso2.model.Tratamiento;

public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    boolean existsByReservaId(Long reservaId);

    Optional<Tratamiento> findByReservaId(Long reservaId);

    @Query("select distinct t from Tratamiento t "
            + "join fetch t.reserva r "
            + "join fetch r.mascota m "
            + "join fetch m.cliente c "
            + "join fetch r.veterinario v "
            + "left join fetch t.medicamentos "
            + "order by r.fecha desc, r.hora desc")
    List<Tratamiento> listarConRelaciones();

    @Query("select t from Tratamiento t "
            + "join fetch t.reserva r "
            + "join fetch r.mascota m "
            + "join fetch m.cliente c "
            + "join fetch r.veterinario v "
            + "left join fetch t.medicamentos "
            + "where t.id = :id")
    Optional<Tratamiento> buscarPorIdConRelaciones(@Param("id") Long id);
}
