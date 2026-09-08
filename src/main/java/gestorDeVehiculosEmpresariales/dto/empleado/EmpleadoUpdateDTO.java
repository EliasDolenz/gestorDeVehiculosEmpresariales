package gestorDeVehiculosEmpresariales.dto.empleado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


import java.time.LocalDate;

public record EmpleadoUpdateDTO(
        @NotBlank(message = "El numero de telefono del empleado no puede estar vacío")
        @Pattern(regexp = "\\d{10}", message = "El número de teléfono debe tener exactamente 10 dígitos") String numeroTelefono,
        @NotNull(message = "El id del departamento del empleado no puede ser nulo")
        Long departamentoId,
        @NotBlank(message = "El correo electrónico del empleado no puede estar vacío")
        String correoElectronico,
        @NotBlank(message = "El puesto del empleado no puede estar vacío")
        String puesto,
        @NotNull(message = "El campo de si el empleado tiene registro de conducir no puede ser nulo")
        Boolean tieneRegistroConducir,
        LocalDate vencimientoLicencia,
        @NotBlank(message = "El pin de carga del empleado no puede estar vacío")
        @Pattern(regexp = "\\d{4}", message = "El pin de carga debe tener exactamente 4 dígitos")
        String pinCarga

        ) {
}
