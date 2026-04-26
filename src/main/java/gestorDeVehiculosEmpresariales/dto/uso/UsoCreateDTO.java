package gestorDeVehiculosEmpresariales.dto.uso;

import java.time.LocalDateTime;

public record UsoCreateDTO(
        Long idVehiculo,
        Long idEmpleado,
        LocalDateTime fechaInicio
) {
}
