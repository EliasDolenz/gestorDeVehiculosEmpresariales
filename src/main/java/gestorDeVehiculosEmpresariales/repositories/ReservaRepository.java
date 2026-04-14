package gestorDeVehiculosEmpresariales.repositories;

import gestorDeVehiculosEmpresariales.entities.Reserva;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.vehiculo.id = :idVehiculo " +
            "AND r.fechaDeFinalizacion > :inicio AND r.fechaDeInicio < :fin")
    Boolean existsOverlapping(Long idVehiculo, @NotNull(message = "Indicar en que día y horario comienza la reserva") LocalDateTime fechaDeInicio, @NotNull(message = "Indicar en que día y horario termina la reserva") LocalDateTime fechaDeFinalizacion);

    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.vehiculo.id = :idVehiculo " +
            "AND r.id <> :idReserva " +
            "AND r.fechaDeFinalizacion > :inicio AND r.fechaDeInicio < :fin")
    boolean existsOverlappingExcludingSelf(Long idVehiculo, Long idReserva, @NotNull(message = "Indicar en que día y horario comienza la reserva") LocalDateTime fechaDeInicio, @NotNull(message = "Indicar en que día y horario termina la reserva") LocalDateTime fechaDeFinalizacion);
}
