package gestorDeVehiculosEmpresariales.dto.vehiculo;

import gestorDeVehiculosEmpresariales.entities.Combustible;
import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record VehiculoUpdateDTO(
        @Positive(message = "El kilometraje actual debe ser un número positivo")
        @NotNull(message = "El kilometraje actual es obligatorio")
        Integer kmActual,
        @NotNull(message = "El estado del seguro es obligatorio")
        LocalDate fechaDeService,
        @NotNull(message = "El vencimiento del service es obligatorio")
        LocalDate vencimientoService,
        @NotNull(message = "Debe indicar si la VTV está vigente o no")
        Boolean vtvVigente,
        @NotNull(message = "El vencimiento de la VTV es obligatorio")
        LocalDate vencimientoVtv,
        @NotNull(message = "El tipo de combustible es obligatorio")
        Combustible nivelCombustible,
        @NotNull(message = "El estado del vehículo es obligatorio")
        EstadoVehiculo estadoVehiculo
) {
}
