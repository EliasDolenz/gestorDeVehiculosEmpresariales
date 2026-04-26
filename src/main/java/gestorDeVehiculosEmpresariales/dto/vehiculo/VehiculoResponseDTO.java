package gestorDeVehiculosEmpresariales.dto.vehiculo;

import gestorDeVehiculosEmpresariales.entities.Combustible;
import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;

import java.time.LocalDate;

public record VehiculoResponseDTO(
        Long id,
        String patente,
        String marca,
        String modelo,
        Integer kmActual,
        LocalDate fechaDeService,
        LocalDate vencimientoService,
        Boolean vtvVigente,
        LocalDate vencimientoVtv,
        Combustible nivelCombustible,
        EstadoVehiculo estadoVehiculo,
        String numeroTarjetaYPF


) {
}
