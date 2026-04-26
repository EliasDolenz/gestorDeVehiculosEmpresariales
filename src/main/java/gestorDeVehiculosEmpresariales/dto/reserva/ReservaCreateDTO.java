package gestorDeVehiculosEmpresariales.dto.reserva;

import java.time.LocalDateTime;

public record ReservaCreateDTO(
        Long idVehiculo,
        LocalDateTime fechaDeInicio,
        LocalDateTime fechaDeFinalizacion,
        Long idEmpleado
) {
}
