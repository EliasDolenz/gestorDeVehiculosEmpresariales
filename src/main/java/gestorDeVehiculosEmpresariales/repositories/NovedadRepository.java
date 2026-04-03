package gestorDeVehiculosEmpresariales.repositories;

import gestorDeVehiculosEmpresariales.entities.Novedad;
import gestorDeVehiculosEmpresariales.entities.Urgencia;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NovedadRepository extends JpaRepository<Novedad, Long> {
    Long countByVehiculoAndUrgencia(@NotNull(message = "Debe indicar a que vehiculo pertenece la novedad") Vehiculo vehiculo, Urgencia urgencia);
}
