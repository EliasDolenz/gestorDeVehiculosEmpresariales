package gestorDeVehiculosEmpresariales.dto.uso;


import jakarta.validation.constraints.NotNull;


public record UsoCreateDTO(
        @NotNull(message = "El id del vehículo no puede ser nulo") Long idVehiculo,
        @NotNull(message = "El id del empleado no puede ser nulo")
        Long idEmpleado
) {
}
