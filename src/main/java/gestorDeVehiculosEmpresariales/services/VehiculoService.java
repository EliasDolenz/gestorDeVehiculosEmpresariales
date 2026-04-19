package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehiculoService {
    private static final Logger logger = LoggerFactory.getLogger(VehiculoService.class);
    private final VehiculoRepository vehiculoRepository;


    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional
    public Vehiculo saveVehiculo(Vehiculo vehiculo) {
        logger.info("Guardando nuevo vehículo con patente: " + vehiculo.getPatente());
        if (vehiculoRepository.existsByPatente(vehiculo.getPatente())) {
            logger.warn("El vehículo con patente " + vehiculo.getPatente() + " ya existe.");
            throw new IllegalArgumentException("El vehículo con Patente " + vehiculo.getPatente() + " ya existe.");
        }

        if (vehiculo.getKmActual() < 0) {
            logger.warn("El kilometraje del vehículo no puede ser negativo. Patente: " + vehiculo.getPatente() + ", KmActual: " + vehiculo.getKmActual());
            throw new IllegalArgumentException("El vehiculo no puede tener kilometraje negativo");
        }
        Vehiculo saved = vehiculoRepository.save(vehiculo);
        logger.info("Vehículo guardado exitosamente con id: " + saved.getId());
        return saved;
    }

    @Transactional
    public Vehiculo updateVehiculo(Long idVehiculo, Vehiculo unVehiculo) {
        logger.info("Actualizando vehículo con id: " + idVehiculo);
        Vehiculo vehiculoExistente = vehiculoRepository.findById(idVehiculo).orElseThrow(() -> {
            logger.warn("El vehículo con id " + idVehiculo + " no existe.");
            throw new IllegalArgumentException("El vehículo con id " + idVehiculo + " no existe.");
        });


        if (!unVehiculo.getPatente().equals(vehiculoExistente.getPatente())) {
            logger.warn("Intento de modificar la patente del vehículo con id " + idVehiculo + ". Patente actual: " + vehiculoExistente.getPatente() + ", Patente nueva: " + unVehiculo.getPatente());
            throw new IllegalArgumentException("No se puede modificar la patente del vehículo.");
        }

        if (unVehiculo.getPatente().isBlank()) {
            logger.warn("Intento de actualizar el vehículo con id " + idVehiculo + " con una patente vacía.");
            throw new IllegalArgumentException("La patente no puede estar vacía");
        }

        if (unVehiculo.getKmActual() < vehiculoExistente.getKmActual()) {
            logger.warn("Intento de actualizar el vehículo con id " + idVehiculo + " con un kilometraje menor al registrado anteriormente. Kilometraje registrado: " + vehiculoExistente.getKmActual() + ", Kilometraje nuevo: " + unVehiculo.getKmActual());
            throw new IllegalArgumentException("El kilometraje actual no puede ser menor al kilometraje registrado anteriormente.");
        }
        vehiculoExistente.setEstadoVehiculo(unVehiculo.getEstadoVehiculo());
        vehiculoExistente.setKmActual(unVehiculo.getKmActual());
        vehiculoExistente.setFechaDeService(unVehiculo.getFechaDeService());
        vehiculoExistente.setVencimientoService(unVehiculo.getVencimientoService());
        vehiculoExistente.setVtvVigente(unVehiculo.getVtvVigente());
        vehiculoExistente.setVencimientoVtv(unVehiculo.getVencimientoVtv());
        vehiculoExistente.setDepartamento(unVehiculo.getDepartamento());

        Vehiculo saved = vehiculoRepository.save(vehiculoExistente);

        logger.info("Vehículo con id " + idVehiculo + " actualizado exitosamente.");
        return saved;

    }

    @Transactional
    public List<Vehiculo> findAllVehiculos() {
        logger.info("Obteniendo lista de todos los vehículos.");

        return vehiculoRepository.findAll();
    }

    @Transactional
    public Vehiculo findVehiculoById(Long idVehiculo) {
        logger.info("Buscando vehículo con id: " + idVehiculo);
        Vehiculo vehiculoExistente = vehiculoRepository.findById(idVehiculo).orElseThrow(() -> {
            logger.warn("El vehículo con id " + idVehiculo + " no existe.");
            throw new IllegalArgumentException("El vehículo con id " + idVehiculo + " no existe.");
        });
        logger.info("Vehículo con id " + idVehiculo + " encontrado exitosamente.");
        return vehiculoExistente;
    }

    @Transactional
    public List<Vehiculo> findVehiculosByDepartamentoId(Long idDepartamento) {
        logger.info("Buscando vehículos del departamento con id: " + idDepartamento);
        List<Vehiculo> vehiculos = vehiculoRepository.findByDepartamentoId(idDepartamento);
        logger.info("Se encontraron " + vehiculos.size() + " vehículos para el departamento con id: " + idDepartamento);
        return vehiculos;
    }

    @Transactional
    public List<Vehiculo> findVehiculosByEstado(EstadoVehiculo estadoVehiculo) {
        logger.info("Buscando vehículos con estado: " + estadoVehiculo);
        List<Vehiculo> vehiculos = vehiculoRepository.findByEstadoVehicular(estadoVehiculo);
        logger.info("Se encontraron " + vehiculos.size() + " vehículos con estado: " + estadoVehiculo);
        return vehiculos;
    }

    @Transactional
    public Boolean deleteVehiculoById(Long idVehiculo) {
        logger.info("Eliminando vehículo con id: " + idVehiculo);

        Vehiculo vehiculoAEliminar = vehiculoRepository.findById(idVehiculo).orElseThrow(
                () -> {
                    logger.warn("El vehículo con id " + idVehiculo + " no existe.");
                    throw new IllegalArgumentException("El vehículo con id " + idVehiculo + " no existe.");
                }
        );

        if (!vehiculoAEliminar.getNovedades().isEmpty()) {
            logger.warn("Intento de eliminar el vehículo con id " + idVehiculo + " que tiene historial de novedades. Cantidad de novedades: " + vehiculoAEliminar.getNovedades().size());
            throw new IllegalArgumentException(
                    "No se puede eliminar el vehículo con id " + idVehiculo + " porque tiene historial de novedades."
            );
        }

        vehiculoRepository.deleteById(idVehiculo);
        logger.info("Vehículo con id " + idVehiculo + " eliminado exitosamente.");
        return Boolean.TRUE;
    }


}
