package gestorDeVehiculosEmpresariales.dto.uso;

import gestorDeVehiculosEmpresariales.entities.EstadoUso;

import java.time.LocalDateTime;

public record UsoUpdateDTO(
        LocalDateTime fechaFinalizacion,
        EstadoUso estadoDeUso
) {
}
