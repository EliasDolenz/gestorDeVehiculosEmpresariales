package gestorDeVehiculosEmpresariales.dto.uso;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UsoCreateDTO(
        @NotNull(message = "El id del vehículo no puede ser nulo") Long idVehiculo,
        @NotNull(message = "El id del empleado no puede ser nulo")
        Long idEmpleado,
        @NotNull(message = "La fecha de inicio del uso no puede ser nula")
        @FutureOrPresent(message = "La fecha de inicio del uso debe ser igual o posterior a la fecha actual")
        LocalDateTime fechaInicio
) {
}
