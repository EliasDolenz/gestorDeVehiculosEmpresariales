package gestorDeVehiculosEmpresariales.dto.uso;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record TerminarUsoDTO(

       @Positive(message = "El kilometraje final debe ser un valor positivo")
        @NotNull(message = "El kilometraje final no debe estar vacío")
        Integer kilometrajeFinal
) {
}
