package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import gestorDeVehiculosEmpresariales.entities.Uso;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.UsoRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Service
public class UsoService {
    private static final Logger logger = LoggerFactory.getLogger(UsoService.class);
    private final EmpleadoRepository empleadoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsoRepository usoRepository;

    public UsoService(EmpleadoRepository empleadoRepository, VehiculoRepository vehiculoRepository, UsoRepository usoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usoRepository = usoRepository;
    }

    @Transactional
    public Uso comenzarUso(Uso unUso) {
        logger.info("Intentando comenzar un nuevo uso para el vehículo con id: " + unUso.getVehiculo().getId() + " por el empleado con id: " + unUso.getEmpleado().getId());
        if (unUso.getVehiculo().getEstadoVehiculo() != EstadoVehiculo.DISPONIBLE) {
            logger.warn("El vehículo con id: " + unUso.getVehiculo().getId() + " no está disponible para su uso. Estado actual: " + unUso.getVehiculo().getEstadoVehiculo());
            throw new IllegalStateException("El vehículo no está disponible para su uso.");
        }

        if (!unUso.getEmpleado().getTieneRegistroConducir()) {
            logger.warn("El empleado con id: " + unUso.getEmpleado().getId() + " no tiene registro de conducir.");
            throw new IllegalStateException("El empleado no tiene registro de conducir.");
        }

        if (unUso.getEmpleado().getVencimientoLicencia().isBefore(ChronoLocalDate.from(LocalDateTime.now()))) {
            logger.warn("La licencia del empleado con id: " + unUso.getEmpleado().getId() + " ha vencido el: " + unUso.getEmpleado().getVencimientoLicencia());
            throw new IllegalStateException("La licencia del empleado ha vencido.");
        }

        if (usoRepository.existsByVehiculoAndFechaFinalizacionIsNull(unUso.getVehiculo())) {
            logger.warn("El vehículo con id: " + unUso.getVehiculo().getId() + " ya está en uso por otro empleado.");
            throw new IllegalStateException("El vehículo ya está en uso.");
        }

        if (usoRepository.existsByEmpleadoAndFechaFinalizacionIsNull(unUso.getEmpleado())) {
            logger.warn("El empleado con id: " + unUso.getEmpleado().getId() + " ya tiene un uso activo.");
            throw new IllegalStateException("El empleado ya tiene un uso activo.");
        }

        unUso.setFechaInicio(LocalDateTime.now());
        unUso.getVehiculo().setEstadoVehiculo(EstadoVehiculo.EN_USO);
        Uso saved = usoRepository.save(unUso);
        logger.info("Comenzando un nuevo uso para el vehículo con id: " + unUso.getVehiculo().getId() + " por el empleado con id: " + unUso.getEmpleado().getId());
        return saved;
    }

    @Transactional
    public Boolean terminarUso(Long idUso, Integer kmActualizados) {
        logger.info("Intentando terminar el uso con id: " + idUso + " y actualizar los kilómetros a: " + kmActualizados);
        Uso uso = usoRepository.findById(idUso).orElseThrow(() -> {
            logger.warn("Uso no encontrado con id: " + idUso);
            throw new IllegalStateException("Uso no encontrado con id: " + idUso);
        });

        if (uso.getFechaFinalizacion() != null) {
            logger.warn("El uso con id: " + idUso + " ya ha sido finalizado el: " + uso.getFechaFinalizacion());
            throw new IllegalStateException("El uso ya ha sido finalizado.");
        }

        if (uso.getVehiculo().getEstadoVehiculo() != EstadoVehiculo.EN_USO) {
            logger.warn("El vehículo con id: " + uso.getVehiculo().getId() + " no está actualmente en uso. Estado actual: " + uso.getVehiculo().getEstadoVehiculo());
            throw new IllegalStateException("El vehículo no está actualmente en uso.");
        }

        if (uso.getVehiculo().getKmActual() > kmActualizados) {
            logger.warn("Los kilómetros actualizados (" + kmActualizados + ") no pueden ser menores que los kilómetros actuales del vehículo (" + uso.getVehiculo().getKmActual() + ").");
            throw new IllegalStateException("Los kilómetros actualizados no pueden ser menores que los kilómetros actuales del vehículo.");
        }

        uso.setFechaFinalizacion(LocalDateTime.now());
        uso.getVehiculo().setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
        uso.getVehiculo().setKmActual(kmActualizados);
        usoRepository.save(uso);

        logger.info("Uso con id: " + idUso + " finalizado exitosamente. Vehículo con id: " + uso.getVehiculo().getId() + " ahora disponible con kilómetros actualizados a: " + kmActualizados);

        return Boolean.TRUE;
    }

    @Transactional
    public Uso findUsoById(Long idUso) {
        logger.info("Buscando uso con id: " + idUso);
        return usoRepository.findById(idUso).orElseThrow(() -> new IllegalStateException("Uso no encontrado con id: " + idUso));
    }

    @Transactional
    public List<Uso> findAllUso() {
        logger.info("Buscando todos los usos registrados");
        return usoRepository.findAll();
    }
}
