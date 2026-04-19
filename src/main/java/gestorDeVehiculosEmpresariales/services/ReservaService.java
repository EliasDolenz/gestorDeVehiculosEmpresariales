package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.Empleado;
import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import gestorDeVehiculosEmpresariales.entities.Reserva;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.ReservaRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {
    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);
    private final ReservaRepository reservaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final EmpleadoRepository empleadoRepository;

    public ReservaService(ReservaRepository reservaRepository, VehiculoRepository vehiculoRepository, EmpleadoRepository empleadoRepository) {
        this.reservaRepository = reservaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public Reserva saveReserva(Reserva unaReserva) {
        logger.info("Guardando nueva reserva para el vehículo con id: " + unaReserva.getVehiculo().getId());

        Empleado empleadoExistente = empleadoRepository.findById(unaReserva.getEmpleado().getId()).orElseThrow(() -> {
            logger.warn("No se encontró el empleado con id: " + unaReserva.getEmpleado().getId());
            throw new IllegalArgumentException("El empleado con id " + unaReserva.getEmpleado().getId() + " no existe.");
        });

        Vehiculo vehiculoExistente = vehiculoRepository.findById(unaReserva.getVehiculo().getId()).orElseThrow(() -> {
            logger.warn("No se encontró el vehículo con id: " + unaReserva.getVehiculo().getId());
            throw new IllegalArgumentException("El vehículo con id " + unaReserva.getVehiculo().getId() + " no existe.");
        });

        if (vehiculoExistente.getEstadoVehiculo() == EstadoVehiculo.EN_REPARACION) {
            logger.warn("El vehículo con id " + vehiculoExistente.getId() + " está en reparación y no puede ser reservado.");
            throw new IllegalArgumentException("El vehículo está en reparación y no puede ser reservado.");
        }

        if (reservaRepository.existsOverlapping(vehiculoExistente.getId(), unaReserva.getFechaDeInicio(), unaReserva.getFechaDeFinalizacion())) {
            logger.warn("El vehículo con id " + vehiculoExistente.getId() + " ya está reservado para el período indicado.");
            throw new IllegalArgumentException("El vehículo ya está reservado para el período indicado.");
        }
        if (unaReserva.getFechaDeInicio().isBefore(LocalDateTime.now())) {
            logger.warn("La fecha de inicio de la reserva no puede ser en el pasado. Fecha de inicio: " + unaReserva.getFechaDeInicio());
            throw new IllegalArgumentException("La fecha de inicio de la reserva no puede ser en el pasado.");
        }

        if (unaReserva.getFechaDeInicio().isAfter(unaReserva.getFechaDeFinalizacion())) {
            logger.warn("La fecha de inicio de la reserva no puede ser después de la fecha de finalización. Fecha de inicio: " + unaReserva.getFechaDeInicio() + ", Fecha de finalización: " + unaReserva.getFechaDeFinalizacion());
            throw new IllegalArgumentException("La fecha de inicio de la reserva no puede ser después de la fecha de finalización.");
        }
        logger.info("Reserva válida para el vehículo con id: " + vehiculoExistente.getId() + ". Guardando reserva...");
        return reservaRepository.save(unaReserva);
    }

    @Transactional
    public Reserva findReservaById(Long idReserva) {
        logger.info("Buscando reserva con id: " + idReserva);
        return reservaRepository.findById(idReserva).orElseThrow(() -> new IllegalArgumentException("La reserva con id " + idReserva + " no existe."));
    }

    @Transactional
    public List<Reserva> findAllReservas() {
        logger.info("Buscando todas las reservas");
        return reservaRepository.findAll();
    }

    @Transactional
    public Boolean deleteReserva(Long id) {
        logger.info("Eliminando reserva con id: " + id);
        Reserva reservaAEliminar = reservaRepository.findById(id).orElseThrow(() -> {
            logger.warn("La reserva con id " + id + " no existe.");
            throw new IllegalArgumentException("La reserva con id " + id + " no existe.");
        });
        reservaRepository.delete(reservaAEliminar);
        logger.info("Reserva con id " + id + " eliminada exitosamente.");
        return Boolean.TRUE;
    }

    @Transactional
    public Reserva updateReserva(Long idReserva, Reserva unaReserva) {
        logger.info("Actualizando reserva con id: " + idReserva);
        Reserva reservaExistente = reservaRepository.findById(idReserva).orElseThrow(() -> {
            logger.warn("La reserva con id " + idReserva + " no existe.");
            throw new IllegalArgumentException("La reserva con id " + idReserva + " no existe.");
        });

        Vehiculo vehiculoExistente = vehiculoRepository.findById(unaReserva.getVehiculo().getId()).orElseThrow(() -> {
            logger.warn("El vehículo con id " + unaReserva.getVehiculo().getId() + " no existe.");
            throw new IllegalArgumentException("El vehículo con id " + unaReserva.getVehiculo().getId() + " no existe.");
        });

        Empleado empleadoExistente = empleadoRepository.findById(unaReserva.getEmpleado().getId()).orElseThrow(() -> {
            logger.warn("El empleado con id " + unaReserva.getEmpleado().getId() + " no existe.");
            throw new IllegalArgumentException("El empleado con id " + unaReserva.getEmpleado().getId() + " no existe.");
        });

        if (reservaRepository.existsOverlappingExcludingSelf(vehiculoExistente.getId(), idReserva, unaReserva.getFechaDeInicio(), unaReserva.getFechaDeFinalizacion())) {
            logger.warn("El vehículo con id " + vehiculoExistente.getId() + " ya está reservado para el período indicado.");
            throw new IllegalArgumentException("El vehículo ya está reservado para el período indicado.");
        }

        if (unaReserva.getFechaDeInicio().isBefore(LocalDateTime.now())) {
            logger.warn("La fecha de inicio de la reserva no puede ser en el pasado. Fecha de inicio: " + unaReserva.getFechaDeInicio());
            throw new IllegalArgumentException("La fecha de inicio de la reserva no puede ser en el pasado.");
        }

        if (unaReserva.getFechaDeInicio().isAfter(unaReserva.getFechaDeFinalizacion())) {
            logger.warn("La fecha de inicio de la reserva no puede ser después de la fecha de finalización. Fecha de inicio: " + unaReserva.getFechaDeInicio() + ", Fecha de finalización: " + unaReserva.getFechaDeFinalizacion());
            throw new IllegalArgumentException("La fecha de inicio de la reserva no puede ser después de la fecha de finalización.");
        }

        reservaExistente.setVehiculo(unaReserva.getVehiculo());
        reservaExistente.setFechaDeInicio(unaReserva.getFechaDeInicio());
        reservaExistente.setFechaDeFinalizacion(unaReserva.getFechaDeFinalizacion());

        Reserva saved = reservaRepository.save(reservaExistente);
        logger.info("Reserva con id " + idReserva + " actualizada exitosamente.");
        return saved;
    }
}
