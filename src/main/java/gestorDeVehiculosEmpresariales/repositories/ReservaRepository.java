package gestorDeVehiculosEmpresariales.repositories;

import gestorDeVehiculosEmpresariales.entities.Reserva;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Boolean existsOverlapping(Long idVehiculo, @NotNull(message = "Indicar en que día y horario comienza la reserva") LocalDateTime fechaDeInicio, @NotNull(message = "Indicar en que día y horario termina la reserva") LocalDateTime fechaDeFinalizacion);

    boolean existsOverlappingExcludingSelf(Long idVehiculo, Long idReserva, @NotNull(message = "Indicar en que día y horario comienza la reserva") LocalDateTime fechaDeInicio, @NotNull(message = "Indicar en que día y horario termina la reserva") LocalDateTime fechaDeFinalizacion);
}
