package gestorDeVehiculosEmpresariales.dto.cargaDeCombustible;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CargaDeCombustibleUpdateDTO(
        @NotNull(message = "Debe indicar la cantidad de litros que se cargaron")
        @Positive(message = "La cantidad de litros debe ser mayor a 0") Double cantidadLitros,
        @NotNull(message = "Debe indicar el kilometraje del auto a la hora de cargar")
        @Positive(message = "El kilometraje del auto a la hora de cargar debe ser mayor a 0")
        Integer kmVehiculo,
        @PastOrPresent(message = "La fecha de recarga no puede ser futura")
        @NotNull(message = "Debe indicar en que fecha se realizó la recarga")
        LocalDateTime fechaRecarga
) {
}
