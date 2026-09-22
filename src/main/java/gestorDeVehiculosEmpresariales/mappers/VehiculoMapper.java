package gestorDeVehiculosEmpresariales.mappers;

import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;

public class VehiculoMapper {
    private VehiculoMapper(){
        // constructor Private para evitar instanciación.
    }
    public static VehiculoResponseDTO toResponseDTO(Vehiculo vehiculo) {
        return new VehiculoResponseDTO(
                vehiculo.getId(),
                vehiculo.getPatente(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getKmActual(),
                vehiculo.getFechaDeService(),
                vehiculo.getVencimientoService(),
                vehiculo.getVtvVigente(),
                vehiculo.getVencimientoVtv(),
                vehiculo.getNivelCombustible(),
                vehiculo.getEstadoVehiculo(),
                vehiculo.getNumeroTarjetaNafta()
        );
    }

    public static VehiculoSimpleDTO toSimpleDTO(Vehiculo vehiculo) {
        return new VehiculoSimpleDTO(
                vehiculo.getId(),
                vehiculo.getPatente(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getEstadoVehiculo()
        );
    }
}
