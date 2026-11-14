package cl.baeza_munoz.PetCare_Caso2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.baeza_munoz.PetCare_Caso2.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByRut(String rut);

    boolean existsByRutAndIdNot(String rut, Long id);

    List<Cliente> findAllByOrderByNombreAsc();

    @Query("""
            select c from Cliente c
            where lower(c.nombre) like lower(concat('%', :q, '%'))
               or lower(c.rut) like lower(concat('%', :q, '%'))
               or lower(c.correo) like lower(concat('%', :q, '%'))
            order by c.nombre
            """)
    List<Cliente> buscar(@Param("q") String q);
}
