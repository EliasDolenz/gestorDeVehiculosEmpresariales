package gestorDeVehiculosEmpresariales.dto.reserva;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;

public record ReservaUpdateDTO(
        @FutureOrPresent LocalDateTime fechaDeFinalizacion
) {
}
