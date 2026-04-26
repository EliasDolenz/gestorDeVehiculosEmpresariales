package gestorDeVehiculosEmpresariales.dto.novedad;

import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;
import gestorDeVehiculosEmpresariales.entities.EstadoNovedad;
import gestorDeVehiculosEmpresariales.entities.Urgencia;

import java.time.LocalDateTime;

public record NovedadResponseDTO(
        Long id,
        String descripcion,
        VehiculoSimpleDTO vehiculo,
        EmpleadoSimpleDTO empleado,
        LocalDateTime fechaReporte,
        EstadoNovedad estadoNovedad,
        Urgencia urgencia
) {
}
