package gestorDeVehiculosEmpresariales.dto.departamento;

import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;

import java.util.Set;

public record DepartamentoResponseDTO(
        Long id,
        String nombre,
        EmpresaSimpleDTO empresa,
        Integer cantidadDeEmpleados,
        Set<EmpleadoSimpleDTO> empleados,
        Integer cantidadDeVehiculos,
        Set<VehiculoSimpleDTO> vehiculos
) {
}
