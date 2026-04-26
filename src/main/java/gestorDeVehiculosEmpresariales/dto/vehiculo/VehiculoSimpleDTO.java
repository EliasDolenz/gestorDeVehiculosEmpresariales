package gestorDeVehiculosEmpresariales.dto.vehiculo;

import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;

public record VehiculoSimpleDTO(
        Long id,
        String patente,
        String marca,
        String modelo,
        EstadoVehiculo estadoVehiculo
) {
}
