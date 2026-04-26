package gestorDeVehiculosEmpresariales.dto.cargaDeCombustible;

import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;

import java.time.LocalDateTime;

public record CargaDeCombustibleSimpleDTO(
        Long id,
        VehiculoSimpleDTO vehiculo,
        Double cantidadLitros,
        LocalDateTime fechaRecarga
) {
}
