package gestorDeVehiculosEmpresariales.dto.reserva;

import java.time.LocalDateTime;

public record ReservaUpdateDTO(
        LocalDateTime fechaDeFinalizacion
) {
}
