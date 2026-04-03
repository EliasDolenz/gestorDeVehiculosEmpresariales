package gestorDeVehiculosEmpresariales.repositories;

import gestorDeVehiculosEmpresariales.entities.Empleado;
import gestorDeVehiculosEmpresariales.entities.Uso;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsoRepository extends JpaRepository<Uso, Long> {
    Boolean existsByVehiculoAndFechaFinalizacionIsNull(@NotNull(message = "El vehiculo debe estar especificado") Vehiculo vehiculo);


    boolean existsByEmpleadoAndFechaFinalizacionIsNull(@NotNull(message = "El empleado debe estar especificado") Empleado empleado);
}
