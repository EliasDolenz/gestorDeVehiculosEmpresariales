package gestorDeVehiculosEmpresariales.dto.departamento;

import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaSimpleDTO;

public record DepartamentoUpdateDTO(
        String nombre,
        EmpresaSimpleDTO empresa
) {
}
