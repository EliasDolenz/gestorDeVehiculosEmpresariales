package gestorDeVehiculosEmpresariales.repositories;

import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Boolean existsByPatente(@NotBlank(message = "La patente no puede estar vacía") String patente);

    List<Vehiculo> findByDepartamentoId(Long departamentoID);

    List<Vehiculo> findByEstadoVehiculo(EstadoVehiculo estadoVehiculo);
}
