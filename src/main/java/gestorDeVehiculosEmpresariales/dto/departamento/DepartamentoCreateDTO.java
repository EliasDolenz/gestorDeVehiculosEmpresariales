package gestorDeVehiculosEmpresariales.dto.departamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepartamentoCreateDTO(
        @NotBlank(message = "El nombre del departamento no debe estar vacío") String nombre,
        @NotNull(message = "El ID de la empresa no debe ser nulo")
        Long empresaId
) {
}
