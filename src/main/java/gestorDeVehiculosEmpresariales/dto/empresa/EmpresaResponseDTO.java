package gestorDeVehiculosEmpresariales.dto.empresa;

import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoSimpleDTO;

import java.util.Set;

public record EmpresaResponseDTO(
        Long id,
        String nombre,
        String direccion,
        Set<DepartamentoSimpleDTO> departamentos

) {
}
