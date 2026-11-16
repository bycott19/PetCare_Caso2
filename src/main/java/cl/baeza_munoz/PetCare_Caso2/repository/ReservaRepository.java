package cl.baeza_munoz.PetCare_Caso2.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.baeza_munoz.PetCare_Caso2.model.EstadoReserva;
import cl.baeza_munoz.PetCare_Caso2.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("select r from Reserva r join fetch r.mascota m join fetch m.cliente c join fetch r.veterinario v order by r.fecha desc, r.hora desc")
    List<Reserva> listarConRelaciones();

    @Query("select r from Reserva r join fetch r.mascota m join fetch m.cliente c join fetch r.veterinario v where r.id = :id")
    Optional<Reserva> buscarPorIdConRelaciones(@Param("id") Long id);

    @Query("select count(r) > 0 from Reserva r "
            + "where r.fecha = :fecha and r.hora = :hora "
            + "and r.estado <> cl.baeza_munoz.PetCare_Caso2.model.EstadoReserva.CANCELADA "
            + "and (:id is null or r.id <> :id)")
    boolean existeConflictoCupo(@Param("fecha") LocalDate fecha,
                                @Param("hora") LocalTime hora,
                                @Param("id") Long id);

    long countByEstado(EstadoReserva estado);

    @Query("select r from Reserva r join fetch r.mascota m join fetch m.cliente c join fetch r.veterinario v "
            + "where r.estado <> cl.baeza_munoz.PetCare_Caso2.model.EstadoReserva.CANCELADA "
            + "and not exists (select t from Tratamiento t where t.reserva.id = r.id) "
            + "order by r.fecha desc, r.hora desc")
    List<Reserva> listarDisponiblesParaTratamiento();
}
