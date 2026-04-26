package gestorDeVehiculosEmpresariales.dto.empleado;

import java.time.LocalDate;

public record EmpleadoUpdateDTO(
        String numeroTelefono,
        Long idDepartamento,
        String correoElectronico,
        String puesto,
        Boolean tieneRegistroConducir,
        LocalDate vencimientoLicencia,
        String pinCarga,
        Long idEmpresa
) {
}
