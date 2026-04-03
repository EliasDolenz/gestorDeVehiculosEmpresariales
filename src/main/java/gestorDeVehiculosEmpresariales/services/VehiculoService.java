package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoService {
    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public Vehiculo saveVehiculo(Vehiculo vehiculo) {
        if (vehiculoRepository.existsByPatente(vehiculo.getPatente())) {
            throw new IllegalArgumentException("El vehículo con Patente " + vehiculo.getPatente() + " ya existe.");
        }

        if (vehiculo.getKmActual() < 0) {
            throw new IllegalArgumentException("El vehiculo no puede tener kilometraje negativo");
        }
        return vehiculoRepository.save(vehiculo);
    }


    public Vehiculo updateVehiculo(Long idVehiculo, Vehiculo unVehiculo) {
        Vehiculo vehiculoExistente = vehiculoRepository.findById(idVehiculo).orElseThrow(() -> new IllegalArgumentException("El vehículo con id " + idVehiculo + " no existe."));


        if (!unVehiculo.getPatente().equals(vehiculoExistente.getPatente())) {
            throw new IllegalArgumentException("No se puede modificar la patente del vehículo.");
        }

        if (unVehiculo.getPatente().isBlank()) {
            throw new IllegalArgumentException("La patente no puede estar vacía");
        }

        if (unVehiculo.getKmActual() < vehiculoExistente.getKmActual()) {
            throw new IllegalArgumentException("El kilometraje actual no puede ser menor al kilometraje registrado anteriormente.");
        }
        vehiculoExistente.setEstadoVehiculo(unVehiculo.getEstadoVehiculo());
        vehiculoExistente.setKmActual(unVehiculo.getKmActual());
        vehiculoExistente.setFechaDeService(unVehiculo.getFechaDeService());
        vehiculoExistente.setVencimientoService(unVehiculo.getVencimientoService());
        vehiculoExistente.setVtvVigente(unVehiculo.getVtvVigente());
        vehiculoExistente.setVencimientoVtv(unVehiculo.getVencimientoVtv());
        vehiculoExistente.setDepartamento(unVehiculo.getDepartamento());

        return vehiculoRepository.save(vehiculoExistente);

    }

    public List<Vehiculo> findAllVehiculos() {

        return vehiculoRepository.findAll();
    }


    public Boolean deleteVehiculoById(Long idVehiculo) {

        Vehiculo vehiculoAEliminar = vehiculoRepository.findById(idVehiculo).orElseThrow(
                () -> new IllegalArgumentException("El vehículo con id " + idVehiculo + " no existe.")
        );


        //Se podría realizar con Cascade en la entidad, se puede modificar mas adelante.
        if (!vehiculoAEliminar.getNovedades().isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el vehículo con id " + idVehiculo + " porque tiene historial de novedades."
            );
        }

        vehiculoRepository.deleteById(idVehiculo);
        return Boolean.TRUE;
    }


}
