package gestorDeVehiculosEmpresariales.dto.uso;

import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;
import gestorDeVehiculosEmpresariales.entities.EstadoUso;

import java.time.LocalDateTime;

public record UsoResponseDTO(
        Long id,
        VehiculoSimpleDTO vehiculo,
        EmpleadoSimpleDTO empleado,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFinalizacion,
        EstadoUso estadoDeUso
) {
}
