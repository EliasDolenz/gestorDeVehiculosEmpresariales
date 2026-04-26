package gestorDeVehiculosEmpresariales.dto.empleado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EmpleadoCreateDTO(
        @NotBlank(message = "El nombre del empleado no puede estar vacío") String nombre,
        @NotBlank(message = "El apellido del empleado no puede estar vacío") String apellido,
        @NotBlank(message = "El número de teléfono del empleado no puede estar vacío")
        String numeroTelefono,
        @NotNull(message = "El departamentoId del empleado no puede ser nulo")
        Long departamentoId,
        @NotBlank(message = "El correo electrónico del empleado no puede estar vacío")
        String correoElectronico,
        @NotBlank(message = "El puesto del empleado no puede estar vacío")
        String puesto,
        @NotNull(message = "El campo tieneRegistroConducir del empleado no puede ser nulo")
        Boolean tieneRegistroConducir,
        @NotBlank(message = "El vencimiento de la licencia del empleado no puede estar vacío")
        LocalDate vencimientoLicencia,
        @NotBlank(message = "El pin de carga del empleado no puede estar vacío")
        String pinCarga,
        @NotNull(message = "El empresaId del empleado no puede ser nulo")
        Long empresaId
) {
}
