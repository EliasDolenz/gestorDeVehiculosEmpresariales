package gestorDeVehiculosEmpresariales.dto.reserva;

import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;

import java.time.LocalDateTime;

public record ReservaResponseDTO(
        Long id,
        VehiculoSimpleDTO vehiculo,
        LocalDateTime fechaDeInicio,
        LocalDateTime fechaDeFinalizacion,
        EmpleadoSimpleDTO empleado
) {
}
