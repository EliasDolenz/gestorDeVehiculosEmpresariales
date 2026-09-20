package gestorDeVehiculosEmpresariales.dto.reserva;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservaUpdateDTO(
        @NotNull(message = "La fecha de inicio no puede ser nula")
        @FutureOrPresent LocalDateTime fechaDeInicio,
        @NotNull(message = "La fecha de finalización no puede ser nula")
        @FutureOrPresent LocalDateTime fechaDeFinalizacion
) {
}
