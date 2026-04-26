package gestorDeVehiculosEmpresariales.dto.cargaDeCombustible;

import java.time.LocalDateTime;

public record CargaDeCombustibleUpdateDTO(
        Double cantidadLitros,
        Integer kmVehiculo,
        Long empleadoId,
        LocalDateTime fechaRecarga
) {
}
