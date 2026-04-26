package gestorDeVehiculosEmpresariales.dto.reserva;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservaCreateDTO(
        @NotNull(message = "El id del vehiculo no puede ser nulo") Long idVehiculo,
        @FutureOrPresent(message = "La fecha de inicio debe ser en el presente o en el futuro")
        LocalDateTime fechaDeInicio,
        @Future(message = "La fecha de finalización debe ser en el futuro")
        LocalDateTime fechaDeFinalizacion,
        @NotNull(message = "El id del empleado no puede ser nulo")
        Long idEmpleado
) {
}
