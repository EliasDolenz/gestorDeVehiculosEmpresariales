package gestorDeVehiculosEmpresariales.dto.empleado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EmpleadoUpdateDTO(
        @NotBlank(message = "El numero de telefono del empleado no puede estar vacío") String numeroTelefono,
        @NotNull(message = "El id del departamento del empleado no puede ser nulo")
        Long idDepartamento,
        @NotBlank(message = "El correo electrónico del empleado no puede estar vacío")
        String correoElectronico,
        @NotBlank(message = "El puesto del empleado no puede estar vacío")
        String puesto,
        @NotNull(message = "El campo de si el empleado tiene registro de conducir no puede ser nulo")
        Boolean tieneRegistroConducir,
        @NotNull(message = "El vencimiento de la licencia del empleado no puede ser nulo")
        LocalDate vencimientoLicencia,
        @NotBlank(message = "El pin de carga del empleado no puede estar vacío")
        String pinCarga,
        @NotNull(message = "El id de la empresa del empleado no puede ser nulo")
        Long idEmpresa
) {
}
