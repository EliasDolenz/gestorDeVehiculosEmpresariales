package gestorDeVehiculosEmpresariales.dto.departamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepartamentoUpdateDTO(
        @NotBlank(message = "El nombre del departamento no puede estar vacío") String nombre,
        @NotNull(message = "Debe ingresar el ID de la empresa a la que pertenece el departamento")
        Long idEmpresa
) {
}
