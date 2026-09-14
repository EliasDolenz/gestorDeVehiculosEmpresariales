package gestorDeVehiculosEmpresariales.dto.novedad;

import gestorDeVehiculosEmpresariales.entities.EstadoNovedad;
import gestorDeVehiculosEmpresariales.entities.Urgencia;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record NovedadCreateDTO(
        @NotBlank(message = "La descripción no puede estar vacía") String descripcion,
        @NotNull(message = "El ID del vehículo es obligatorio")
        Long idVehiculo,
        @NotNull(message = "El ID del empleado es obligatorio")
        Long idEmpleado,
        @NotNull(message = "El estado de la novedad es obligatorio")
        EstadoNovedad estadoNovedad,
        @NotNull(message = "El nivel de urgencia es obligatorio")
        Urgencia urgencia

) {
}
