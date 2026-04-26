package gestorDeVehiculosEmpresariales.dto.vehiculo;

import gestorDeVehiculosEmpresariales.entities.Combustible;
import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record VehiculoCreateDTO(
        @NotBlank(message = "La patente no puede estar vacía")
        String patente,
        @NotBlank(message = "La marca no puede estar vacía")
        String marca,
        @NotBlank(message = "El modelo no puede estar vacío")
        String modelo,
        @NotNull(message = "El km actual no puede ser nulo")
        @Positive(message = "El km actual debe ser un número positivo")
        Integer kmActual,
        @NotNull(message = "La fecha de service no puede ser nula")
        LocalDate fechaDeService,
        @NotNull(message = "El vencimiento del service no puede ser nulo")
        LocalDate vencimientoService,
        @NotNull(message = "Debe indicar si la VTV está vigente o no")
        Boolean vtvVigente,
        @NotNull(message = "El vencimiento de la VTV no puede ser nulo")
        LocalDate vencimientoVtv,
        @NotNull(message = "El departamento al que pertenece el vehículo no puede ser nulo")
        Long departamentoId,
        @NotNull(message = "El nivel de combustible no puede ser nulo")
        Combustible nivelCombustible,
        @NotNull(message = "El estado del vehículo no puede ser nulo")
        EstadoVehiculo estadoVehiculo,
        @NotBlank(message = "La marca de la tarjeta YPF no puede estar vacía")
        String numeroTarjetaYPF
) {
}
