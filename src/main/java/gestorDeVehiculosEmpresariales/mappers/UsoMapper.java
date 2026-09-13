package gestorDeVehiculosEmpresariales.mappers;

import gestorDeVehiculosEmpresariales.dto.uso.UsoResponseDTO;
import gestorDeVehiculosEmpresariales.entities.Uso;

public class UsoMapper {
    private UsoMapper() {
        // Private constructor to prevent instantiation
    }


    public static UsoResponseDTO toResponseDTO(Uso uso) {
        return new UsoResponseDTO(
                uso.getId(),
                VehiculoMapper.toSimpleDTO(uso.getVehiculo()),
                EmpleadoMapper.toSimpleDTO(uso.getEmpleado()),
                uso.getFechaInicio(),
                uso.getFechaFinalizacion(),
                uso.getEstadoDeUso()
        );
    }
}
