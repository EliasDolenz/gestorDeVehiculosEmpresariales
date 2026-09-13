package gestorDeVehiculosEmpresariales.mappers;

import gestorDeVehiculosEmpresariales.dto.novedad.NovedadResponseDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadSimpleDTO;
import gestorDeVehiculosEmpresariales.entities.Novedad;

public class NovedadMapper {
    private NovedadMapper(){
        // constructor Private para evitar instanciación.
    }

    public static NovedadSimpleDTO toSimpleDTO(Novedad novedad) {
        return new NovedadSimpleDTO(
                novedad.getId(),
                novedad.getDescripcion(),
                VehiculoMapper.toSimpleDTO(novedad.getVehiculo()),
                novedad.getFechaReporte(),
                novedad.getEstadoNovedad()
        );
    }

    public static NovedadResponseDTO toResponseDTO(Novedad novedad) {
        return new NovedadResponseDTO(
                novedad.getId(),
                novedad.getDescripcion(),
                VehiculoMapper.toSimpleDTO(novedad.getVehiculo()),
                EmpleadoMapper.toSimpleDTO(novedad.getEmpleado()),
                novedad.getFechaReporte(),
                novedad.getEstadoNovedad(),
                novedad.getUrgencia()
        );
    }
}
