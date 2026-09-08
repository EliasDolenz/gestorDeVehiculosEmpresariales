package gestorDeVehiculosEmpresariales.mappers;

import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoSimpleDTO;
import gestorDeVehiculosEmpresariales.entities.Empleado;

public class EmpleadoMapper {
    private EmpleadoMapper() {
        // constructor Private para evitar instanciación.
    }


    public static EmpleadoSimpleDTO toSimpleDTO(Empleado empleado) {
        return new EmpleadoSimpleDTO(
                empleado.getId(),
                empleado.getNombre(),
                empleado.getApellido()
        );
    }

    public static EmpleadoResponseDTO toResponseDTO(Empleado empleado) {
        return new EmpleadoResponseDTO(
                empleado.getId(),
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getNumeroTelefono(),
                DepartamentoMapper.toSimpleDTO(empleado.getDepartamento()),
                empleado.getCorreoElectronico(),
                empleado.getPuesto(),
                empleado.getTieneRegistroConducir(),
                empleado.getVencimientoLicencia(),
                EmpresaMapper.toSimpleDTO(empleado.getEmpresa())
        );
    }
}
