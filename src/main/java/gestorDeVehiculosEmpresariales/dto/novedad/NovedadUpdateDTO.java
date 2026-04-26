package gestorDeVehiculosEmpresariales.dto.novedad;

import gestorDeVehiculosEmpresariales.entities.EstadoNovedad;
import gestorDeVehiculosEmpresariales.entities.Urgencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NovedadUpdateDTO(
        @NotBlank(message = "La descripción no puede estar vacía") String descripcion,
        @NotNull(message = "El estado de la novedad no puede ser nulo")
        EstadoNovedad estadoNovedad,
        @NotNull(message = "Debe indicar la urgencia de la novedad")
        Urgencia urgencia
) {
}
