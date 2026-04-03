package gestorDeVehiculosEmpresariales.repositories;

import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Boolean existsByPatente(@NotBlank(message = "La patente no puede estar vacía") String patente);

}
