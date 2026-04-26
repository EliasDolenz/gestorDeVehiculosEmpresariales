package gestorDeVehiculosEmpresariales.dto.empresa;

import jakarta.validation.constraints.NotBlank;

public record EmpresaUpdateDTO(
        @NotBlank(message = "El nombre de la empresa no debe estar en blanco") String nombre,
        @NotBlank(message = "La dirección de la empresa no debe estar en blanco")
        String direccion
) {
}
