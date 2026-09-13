package gestorDeVehiculosEmpresariales.mappers;

import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaResponseDTO;
import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaSimpleDTO;
import gestorDeVehiculosEmpresariales.entities.Empresa;
import java.util.stream.Collectors;
public class EmpresaMapper {
    private EmpresaMapper() {
        // constructor Private para evitar instanciacion.
    }

    public static EmpresaSimpleDTO toSimpleDTO(Empresa empresa) {
        return new EmpresaSimpleDTO(
                empresa.getId(),
                empresa.getNombre(),
                empresa.getDireccion()
        );
    }

    public static EmpresaResponseDTO toResponseDTO(Empresa empresa) {
        return new EmpresaResponseDTO(
                empresa.getId(),
                empresa.getNombre(),
                empresa.getDireccion(),
                empresa.getDepartamentos().stream().map(DepartamentoMapper::toSimpleDTO).collect(Collectors.toSet())
        );
    }
}