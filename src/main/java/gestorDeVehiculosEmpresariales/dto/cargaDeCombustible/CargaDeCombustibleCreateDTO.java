package gestorDeVehiculosEmpresariales.dto.cargaDeCombustible;

import java.time.LocalDateTime;

public record CargaDeCombustibleCreateDTO(
        Long idVehiculo,
        Double cantidadLitros,
        Integer kmVehiculo,
        Long idEmpleado,
        LocalDateTime fechaRecarga
) {
}
