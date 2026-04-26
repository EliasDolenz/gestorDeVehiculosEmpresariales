package gestorDeVehiculosEmpresariales.dto.uso;

import gestorDeVehiculosEmpresariales.entities.EstadoUso;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record UsoUpdateDTO(

        @FutureOrPresent(message = "La fecha de inicio no puede ser en el pasado")
        @NotNull(message = "La fecha de finalización no debe estar vacío")
        LocalDateTime fechaFinalizacion,
        @NotNull(message = "Debe indicar el estado de uso del vehículo")
        EstadoUso estadoDeUso
) {
}
