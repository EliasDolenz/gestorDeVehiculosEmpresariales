package gestorDeVehiculosEmpresariales.dto.vehiculo;

import gestorDeVehiculosEmpresariales.entities.Combustible;
import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;

import java.time.LocalDate;

public record VehiculoUpdateDTO(
        Integer kmActual,
        LocalDate fechaDeService,
        LocalDate vencimientoService,
        Boolean vtvVigente,
        LocalDate vencimientoVtv,
        Combustible nivelCombustible,
        EstadoVehiculo estadoVehiculo
) {
}
