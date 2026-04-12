package gestorDeVehiculosEmpresariales.repositories;

import gestorDeVehiculosEmpresariales.entities.Empleado;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    boolean existsByCorreoElectronico(@NotBlank String correoElectronico);

    boolean existsByNumeroTelefono(@NotBlank(message = "El numero de telefono no puede estar vacío") String numeroTelefono);

    Integer countByDepartamentoId(Long b);
}
