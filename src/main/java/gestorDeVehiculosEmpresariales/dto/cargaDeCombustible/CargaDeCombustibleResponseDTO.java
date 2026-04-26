package gestorDeVehiculosEmpresariales.dto.cargaDeCombustible;

import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;

import java.time.LocalDateTime;

public record CargaDeCombustibleResponseDTO(
        Long id,
        VehiculoSimpleDTO vehiculo,
        Double cantidadLitros,
        Integer kmVehiculo,
        EmpleadoSimpleDTO empleado,
        LocalDateTime fechaRecarga
) {
}
