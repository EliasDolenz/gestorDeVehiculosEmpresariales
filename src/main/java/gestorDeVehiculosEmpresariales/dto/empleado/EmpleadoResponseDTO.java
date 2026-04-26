package gestorDeVehiculosEmpresariales.dto.empleado;

import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaSimpleDTO;

import java.time.LocalDate;

public record EmpleadoResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String numeroTelefono,
        DepartamentoSimpleDTO departamento,
        String correoElectronico,
        String puesto,
        Boolean tieneRegistroConducir,
        LocalDate vencimientoLicencia,
        EmpresaSimpleDTO empresa
) {
}
