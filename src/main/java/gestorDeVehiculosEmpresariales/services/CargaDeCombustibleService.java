package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.*;
import gestorDeVehiculosEmpresariales.repositories.CargaDeCombustibleRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CargaDeCombustibleService {
    private static final Logger logger = LoggerFactory.getLogger(CargaDeCombustible.class);
    private final VehiculoRepository vehiculoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final CargaDeCombustibleRepository cargaDeCombustibleRepository;

    public CargaDeCombustibleService(VehiculoRepository vehiculoRepository, EmpleadoRepository empleadoRepository, CargaDeCombustibleRepository cargaDeCombustibleRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.empleadoRepository = empleadoRepository;
        this.cargaDeCombustibleRepository = cargaDeCombustibleRepository;
    }

    @Transactional
    public CargaDeCombustible findCargaById(Long idCarga) {
        logger.info("Buscando carga de combustible con id: " + idCarga);

        return cargaDeCombustibleRepository.findById(idCarga).orElseThrow(() -> {
            logger.warn("No se encontró la carga de combustible con id: " + idCarga);
            return new RuntimeException("No se encontró la carga de combustible con id: " + idCarga);
        });
    }

    @Transactional(readOnly = true)
    public List<CargaDeCombustible> findAllCargas() {
        logger.info("Buscando todas las cargas de combustible");
        return cargaDeCombustibleRepository.findAll();
    }

    @Transactional
    public CargaDeCombustible saveCarga(CargaDeCombustible cargaDeCombustible) {
        logger.info("Guardando nueva carga de combustible para el vehículo con id: " + cargaDeCombustible.getVehiculo().getId());

        Vehiculo vehiculoReal = vehiculoRepository.findById(cargaDeCombustible.getVehiculo().getId()).orElseThrow(() -> {
            logger.warn("No se encontro el vehiculo con el ID: " + cargaDeCombustible.getVehiculo().getId());
            throw new RuntimeException("No se encontró el vehículo con id: " + cargaDeCombustible.getVehiculo().getId());
        });

        Empleado empleadoReal = empleadoRepository.findById(cargaDeCombustible.getEmpleado().getId()).orElseThrow(() -> {
            logger.warn("No se encontro el empleado con el ID: " + cargaDeCombustible.getEmpleado().getId());
            throw new RuntimeException("No se encontró el empleado con id: " + cargaDeCombustible.getEmpleado().getId());
        });

        if (vehiculoReal.getEstadoVehiculo() == EstadoVehiculo.EN_REPARACION) {
            logger.warn("No se puede cargar combustible a un vehículo que se encuentra en reparación");
            throw new RuntimeException("No se puede cargar combustible a un vehículo que se encuentra en reparación");
        }

        if (cargaDeCombustible.getKmVehiculo() <= vehiculoReal.getKmActual()) {
            logger.warn("El kilometraje de carga (" + cargaDeCombustible.getKmVehiculo() + ") debe ser mayor al actual (" + vehiculoReal.getKmActual() + ")");
            throw new RuntimeException("El kilometraje de carga debe ser mayor al actual (" + vehiculoReal.getKmActual() + ")");
        }


        vehiculoReal.setKmActual(cargaDeCombustible.getKmVehiculo());
        vehiculoReal.setNivelCombustible(Combustible.LLENO);
        vehiculoRepository.save(vehiculoReal);
        cargaDeCombustible.setVehiculo(vehiculoReal);
        cargaDeCombustible.setEmpleado(empleadoReal);
        cargaDeCombustible.setFechaRecarga(java.time.LocalDateTime.now());

        CargaDeCombustible saved = this.cargaDeCombustibleRepository.save(cargaDeCombustible);
        logger.info("Guardado Exitoso - ID:{}", cargaDeCombustible.getId());
        return saved;

    }

    @Transactional
    public CargaDeCombustible updateCarga(Long idCarga, CargaDeCombustible carga) {
        logger.info("Actualizando carga de combustible con id: " + idCarga);
        CargaDeCombustible cargaExistente = cargaDeCombustibleRepository.findById(idCarga).orElseThrow(() -> {
            logger.warn("No se encontró la carga de combustible con id: " + idCarga);
            return new RuntimeException("No se encontró la carga de combustible con id: " + idCarga);
        });

        Vehiculo vehiculoReal = vehiculoRepository.findById(carga.getVehiculo().getId()).orElseThrow(() -> {
            logger.warn("No se encontró el vehículo con id: " + carga.getVehiculo().getId());

            return new RuntimeException("No se encontró el vehículo con id: " + carga.getVehiculo().getId());
        });

        if (vehiculoReal.getEstadoVehiculo() != EstadoVehiculo.EN_USO) {
            logger.warn("No se puede modificar la carga de combustible de un vehículo que no se encuentra en uso");
            throw new RuntimeException("No se puede modificar la carga de combustible de un vehículo que no se encuentra en uso");
        }
        Empleado empleadoReal = empleadoRepository.findById(carga.getEmpleado().getId()).orElseThrow(() -> {
            logger.warn("No se encontró el empleado con id: " + carga.getEmpleado().getId());
            return new RuntimeException("No se encontró el empleado con id: " + carga.getEmpleado().getId());
        });

        if (carga.getVehiculo() == null || carga.getEmpleado() == null) {
            logger.warn("El vehículo y el empleado no pueden ser nulos");
            throw new RuntimeException("El vehículo y el empleado no pueden ser nulos");
        }


        if (carga.getKmVehiculo() < vehiculoReal.getKmActual()) {
            logger.warn("El kilometraje del vehículo a la hora de cargar (" + carga.getKmVehiculo() + ") no puede ser menor al kilometraje actual del vehículo (" + vehiculoReal.getKmActual() + ")");
            throw new RuntimeException("El kilometraje del vehículo a la hora  de cargar no puede ser menor al kilometraje actual del vehículo");
        }

        vehiculoReal.setKmActual(carga.getKmVehiculo());

        cargaExistente.setVehiculo(vehiculoReal);
        cargaExistente.setCantidadLitros(carga.getCantidadLitros());
        cargaExistente.setKmVehiculo(carga.getKmVehiculo());
        vehiculoRepository.save(vehiculoReal);
        CargaDeCombustible update = cargaDeCombustibleRepository.save(cargaExistente);
        logger.info("Carga de combustible actualizada exitosamente con id: " + update.getId());
        return update;
    }

    @Transactional
    public Boolean deleteCargaById(Long idCarga) {
        logger.info("Eliminando carga de combustible con id: " + idCarga);
        if (!cargaDeCombustibleRepository.existsById(idCarga)) {
            logger.warn("No se encontró la carga de combustible con id: " + idCarga);
            throw new RuntimeException("No se encontró la carga de combustible con id: " + idCarga);
        }
        cargaDeCombustibleRepository.deleteById(idCarga);
        logger.info("Carga de combustible con id: " + idCarga + " eliminada exitosamente.");
        return Boolean.TRUE;
    }
}
