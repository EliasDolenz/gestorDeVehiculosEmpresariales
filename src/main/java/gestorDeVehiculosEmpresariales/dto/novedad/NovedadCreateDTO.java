package gestorDeVehiculosEmpresariales.dto.novedad;

import gestorDeVehiculosEmpresariales.entities.EstadoNovedad;
import gestorDeVehiculosEmpresariales.entities.Urgencia;

import java.time.LocalDateTime;

public record NovedadCreateDTO(
        String descripcion,
        Long idVehiculo,
        Long idEmpleado,
        LocalDateTime fechaReporte,
        EstadoNovedad estadoNovedad,
        Urgencia urgencia

) {
}
