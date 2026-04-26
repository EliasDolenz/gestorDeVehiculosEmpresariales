package gestorDeVehiculosEmpresariales.dto.reserva;

import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;

import java.time.LocalDateTime;

public record ReservaSimpleDTO(
        Long id,
        VehiculoSimpleDTO vehiculo,
        LocalDateTime fechaDeInicio,
        LocalDateTime fechaDeFinalizacion,
        EmpleadoSimpleDTO empleado
) {
}
