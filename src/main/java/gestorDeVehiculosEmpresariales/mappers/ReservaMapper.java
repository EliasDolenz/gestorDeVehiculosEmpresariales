package gestorDeVehiculosEmpresariales.mappers;

import gestorDeVehiculosEmpresariales.dto.reserva.ReservaResponseDTO;
import gestorDeVehiculosEmpresariales.entities.Reserva;

public class ReservaMapper {
    private ReservaMapper() {
        // constructor Private para evitar instanciación.
    }

    public static ReservaResponseDTO toResponseDTO(Reserva reserva) {
        return new ReservaResponseDTO(
                reserva.getId(),
                VehiculoMapper.toSimpleDTO(reserva.getVehiculo()),
                reserva.getFechaDeInicio(),
                reserva.getFechaDeFinalizacion(),
                EmpleadoMapper.toSimpleDTO(reserva.getEmpleado())
        );
    }
}
