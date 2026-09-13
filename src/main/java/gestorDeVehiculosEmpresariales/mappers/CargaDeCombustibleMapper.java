package gestorDeVehiculosEmpresariales.mappers;

import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleResponseDTO;
import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleSimpleDTO;
import gestorDeVehiculosEmpresariales.entities.CargaDeCombustible;

public class CargaDeCombustibleMapper {
    private CargaDeCombustibleMapper() {
        // constructor Private para evitar instanciación
    }

    public static CargaDeCombustibleSimpleDTO toSimpleDTO(CargaDeCombustible cargaDeCombustible) {
        return new CargaDeCombustibleSimpleDTO(
                cargaDeCombustible.getId(),
                VehiculoMapper.toSimpleDTO(cargaDeCombustible.getVehiculo()),
                cargaDeCombustible.getCantidadLitros(),
                cargaDeCombustible.getFechaRecarga()
        );
    }

    public static CargaDeCombustibleResponseDTO toResponseDTO(CargaDeCombustible cargaDeCombustible) {
        return new CargaDeCombustibleResponseDTO(
                cargaDeCombustible.getId(),
                VehiculoMapper.toSimpleDTO(cargaDeCombustible.getVehiculo()),
                cargaDeCombustible.getCantidadLitros(),
                cargaDeCombustible.getKmVehiculo(),
                EmpleadoMapper.toSimpleDTO(cargaDeCombustible.getEmpleado()),
                cargaDeCombustible.getFechaRecarga()
        );
    }
}
