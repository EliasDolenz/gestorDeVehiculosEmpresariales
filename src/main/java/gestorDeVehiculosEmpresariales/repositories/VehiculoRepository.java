package gestorDeVehiculosEmpresariales.repositories;

import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Boolean existsByPatente(String patente);

    List<Vehiculo> findByDepartamentoId(Long departamentoID);

    List<Vehiculo> findByEstadoVehiculo(EstadoVehiculo estadoVehiculo);

    Integer countByDepartamentoId(Long idDepartamento);
}
