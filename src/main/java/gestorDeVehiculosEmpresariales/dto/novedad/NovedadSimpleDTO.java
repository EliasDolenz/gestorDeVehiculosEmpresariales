package gestorDeVehiculosEmpresariales.dto.novedad;

import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;
import gestorDeVehiculosEmpresariales.entities.EstadoNovedad;

import java.time.LocalDateTime;

public record NovedadSimpleDTO(
        Long id,
        String descripcion,
        VehiculoSimpleDTO vehiculo,
        LocalDateTime fechaReporte,
        EstadoNovedad estadoNovedad
) {
}
