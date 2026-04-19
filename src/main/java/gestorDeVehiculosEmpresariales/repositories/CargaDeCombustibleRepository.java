package gestorDeVehiculosEmpresariales.repositories;

import gestorDeVehiculosEmpresariales.entities.CargaDeCombustible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargaDeCombustibleRepository extends JpaRepository<CargaDeCombustible, Long> {
    List<CargaDeCombustible> findByVehiculoId(Long idVehiculo);


    List<CargaDeCombustible> findByVehiculoIdOrderByFechaRecargaDesc(Long idVehiculo);
}
