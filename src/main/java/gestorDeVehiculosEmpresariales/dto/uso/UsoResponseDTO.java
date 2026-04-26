package gestorDeVehiculosEmpresariales.dto.uso;

import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;

import java.time.LocalDateTime;

public record UsoResponseDTO(
        Long id,
        VehiculoSimpleDTO vehiculo,
        EmpleadoSimpleDTO empleado,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFinalizacion,
        String estadoDeUso
) {
}
