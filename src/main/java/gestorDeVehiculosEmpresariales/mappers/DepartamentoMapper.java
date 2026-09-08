package gestorDeVehiculosEmpresariales.mappers;

import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoSimpleDTO;

import gestorDeVehiculosEmpresariales.entities.Departamento;

import java.util.stream.Collectors;

public class DepartamentoMapper {
    private DepartamentoMapper() {
        // constructor Private para evitar instanciación.
    }

    public static DepartamentoSimpleDTO toSimpleDTO(Departamento departamento) {
        return new DepartamentoSimpleDTO(
                departamento.getId(),
                departamento.getNombre()
        );
    }

    public static DepartamentoResponseDTO toResponseDTO(Departamento departamento) {
        return new DepartamentoResponseDTO(
                departamento.getId(),
                departamento.getNombre(),
                EmpresaMapper.toSimpleDTO(departamento.getEmpresa()),
                departamento.getEmpleados().size(),
                departamento.getEmpleados().stream().map(EmpleadoMapper::toSimpleDTO).collect(Collectors.toSet()),
                departamento.getVehiculos().size(),
                departamento.getVehiculos().stream().map(VehiculoMapper::toSimpleDTO).collect(Collectors.toSet())
        );
    }

}
