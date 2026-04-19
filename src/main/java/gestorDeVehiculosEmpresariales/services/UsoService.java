package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.Empleado;
import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import gestorDeVehiculosEmpresariales.entities.Uso;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.ReservaRepository;
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
    private final ReservaRepository reservaRepository;

    public UsoService(EmpleadoRepository empleadoRepository, VehiculoRepository vehiculoRepository, UsoRepository usoRepository, ReservaRepository reservaRepository) {
        this.empleadoRepository = empleadoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usoRepository = usoRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional
    public Uso comenzarUso(Uso unUso) {
        logger.info("Intentando comenzar un nuevo uso para el vehículo con id: " + unUso.getVehiculo().getId() + " por el empleado con id: " + unUso.getEmpleado().getId());

        Vehiculo vehiculoReal = vehiculoRepository.findById(unUso.getVehiculo().getId()).orElseThrow(() -> {
            logger.warn("Vehículo no encontrado con id: " + unUso.getVehiculo().getId());
            throw new IllegalStateException("Vehículo no encontrado con id: " + unUso.getVehiculo().getId());
        });

        Empleado empleadoReal = empleadoRepository.findById(unUso.getEmpleado().getId()).orElseThrow(() -> {
            logger.warn("Empleado no encontrado con id: " + unUso.getEmpleado().getId());
            throw new IllegalStateException("Empleado no encontrado con id: " + unUso.getEmpleado().getId());
        });

        if (reservaRepository.existsOverlapping(vehiculoReal.getId(), unUso.getFechaInicio(), unUso.getFechaFinalizacion().plusHours(1))) {
            logger.warn("El vehículo con id: " + unUso.getVehiculo().getId() + " tiene una reserva que se superpone con el período de uso solicitado.");
            throw new IllegalStateException("El vehículo tiene una reserva que se superpone con el período de uso solicitado.");
        }

        if (vehiculoReal.getEstadoVehiculo() != EstadoVehiculo.DISPONIBLE) {
            logger.warn("El vehículo con id: " + unUso.getVehiculo().getId() + " no está disponible para su uso. Estado actual: " + vehiculoReal.getEstadoVehiculo());
            throw new IllegalStateException("El vehículo no está disponible para su uso.");
        }

        if (!empleadoReal.getTieneRegistroConducir()) {
            logger.warn("El empleado con id: " + empleadoReal.getId() + " no tiene registro de conducir.");
            throw new IllegalStateException("El empleado no tiene registro de conducir.");
        }

        if (empleadoReal.getVencimientoLicencia() == null) {
            logger.warn("El empleado con id: " + empleadoReal.getId() + " no tiene fecha de vencimiento de licencia registrada.");
            throw new IllegalStateException("El empleado no tiene fecha de vencimiento de licencia registrada.");
        }

        if (empleadoReal.getVencimientoLicencia().isBefore(ChronoLocalDate.from(LocalDateTime.now()))) {
            logger.warn("La licencia del empleado con id: " + unUso.getEmpleado().getId() + " ha vencido el: " + empleadoReal.getVencimientoLicencia());
            throw new IllegalStateException("La licencia del empleado ha vencido.");
        }

        if (usoRepository.existsByVehiculoAndFechaFinalizacionIsNull(vehiculoReal)) {
            logger.warn("El vehículo con id: " + vehiculoReal.getId() + " ya está en uso por otro empleado.");
            throw new IllegalStateException("El vehículo ya está en uso.");
        }

        if (usoRepository.existsByEmpleadoAndFechaFinalizacionIsNull(empleadoReal)) {
            logger.warn("El empleado con id: " + empleadoReal.getId() + " ya tiene un uso activo.");
            throw new IllegalStateException("El empleado ya tiene un uso activo.");
        }
        unUso.setEmpleado(empleadoReal);
        unUso.setVehiculo(vehiculoReal);
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

    @Transactional
    public Boolean deleteUso(Long idUso) {
        logger.info("Intentando eliminar el uso con id: " + idUso);
        Uso uso = usoRepository.findById(idUso).orElseThrow(() -> {
            logger.warn("Uso no encontrado con id: " + idUso);
            throw new IllegalStateException("Uso no encontrado con id: " + idUso);

        });
        this.usoRepository.delete(uso);
        logger.info("Uso con id: " + idUso + " eliminado exitosamente.");
        return Boolean.TRUE;
    }
}
