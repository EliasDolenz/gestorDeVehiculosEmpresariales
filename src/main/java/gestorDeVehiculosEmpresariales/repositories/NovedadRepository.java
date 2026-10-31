package gestorDeVehiculosEmpresariales.repositories;

import gestorDeVehiculosEmpresariales.entities.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NovedadRepository extends JpaRepository<Novedad, Long> {
    Long countByVehiculoAndUrgenciaAndEstadoNovedadNot(@NotNull(message = "Debe indicar a que vehiculo pertenece la novedad") Vehiculo vehiculo, Urgencia urgencia, EstadoNovedad estadoNovedad);
}
