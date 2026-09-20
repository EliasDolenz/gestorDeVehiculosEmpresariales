package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.dto.reserva.ReservaCreateDTO;
import gestorDeVehiculosEmpresariales.dto.reserva.ReservaResponseDTO;
import gestorDeVehiculosEmpresariales.dto.reserva.ReservaUpdateDTO;
import gestorDeVehiculosEmpresariales.entities.Empleado;
import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import gestorDeVehiculosEmpresariales.entities.Reserva;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import gestorDeVehiculosEmpresariales.exceptions.RecursoNoEncontradoException;
import gestorDeVehiculosEmpresariales.exceptions.ReglaDeNegocioException;
import gestorDeVehiculosEmpresariales.mappers.ReservaMapper;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.ReservaRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ReservaResponseDTO saveReserva(ReservaCreateDTO unaReserva) {
        logger.info("Guardando nueva reserva para el vehículo con id: " + unaReserva.idVehiculo() + " y empleado con id: " + unaReserva.idEmpleado());

        if (unaReserva.fechaDeInicio().isAfter(unaReserva.fechaDeFinalizacion())) {
            logger.warn("La fecha de inicio de la reserva no puede ser después de la fecha de finalización. Fecha de inicio: " + unaReserva.fechaDeInicio() + ", Fecha de finalización: " + unaReserva.fechaDeFinalizacion());
            throw new ReglaDeNegocioException("La fecha de inicio de la reserva no puede ser después de la fecha de finalización.");
        }

        Empleado empleadoExistente = empleadoRepository.findById(unaReserva.idEmpleado()).orElseThrow(() -> {
            logger.warn("No se encontró el empleado con id: " + unaReserva.idEmpleado());
            return new RecursoNoEncontradoException("El empleado con id " + unaReserva.idEmpleado() + " no existe.");
        });

        Vehiculo vehiculoExistente = vehiculoRepository.findById(unaReserva.idVehiculo()).orElseThrow(() -> {
            logger.warn("No se encontró el vehículo con id: " + unaReserva.idVehiculo());
            return new RecursoNoEncontradoException("El vehículo con id " + unaReserva.idVehiculo() + " no existe.");
        });

        if (vehiculoExistente.getEstadoVehiculo() == EstadoVehiculo.EN_REPARACION) {
            logger.warn("El vehículo con id " + vehiculoExistente.getId() + " está en reparación y no puede ser reservado.");
            throw new ReglaDeNegocioException("El vehículo está en reparación y no puede ser reservado.");
        }

        if (reservaRepository.existsOverlapping(vehiculoExistente.getId(), unaReserva.fechaDeInicio(), unaReserva.fechaDeFinalizacion())) {
            logger.warn("El vehículo con id " + vehiculoExistente.getId() + " ya está reservado para el período indicado.");
            throw new ReglaDeNegocioException("El vehículo ya está reservado para el período indicado.");
        }
        logger.info("Reserva válida para el vehículo con id: " + vehiculoExistente.getId() + ". Guardando reserva...");
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setEmpleado(empleadoExistente);
        nuevaReserva.setVehiculo(vehiculoExistente);
        nuevaReserva.setFechaDeInicio(unaReserva.fechaDeInicio());
        nuevaReserva.setFechaDeFinalizacion(unaReserva.fechaDeFinalizacion());

        Reserva guardada = reservaRepository.save(nuevaReserva);

        return ReservaMapper.toResponseDTO(guardada);
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO findReservaById(Long idReserva) {
        logger.info("Buscando reserva con id: " + idReserva);

        Reserva reserva = obtenerReservaPorId(idReserva);
        return ReservaMapper.toResponseDTO(reserva);
    }


    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> findAllReservas() {

        logger.info("Buscando todas las reservas");
        List<Reserva> reservas = reservaRepository.findAll();

        List<ReservaResponseDTO> reservasDTO = reservas.stream().map(ReservaMapper::toResponseDTO).toList();

        return reservasDTO;
    }

    @Transactional
    public Boolean deleteReserva(Long id) {
        logger.info("Eliminando reserva con id: " + id);
        Reserva reservaAEliminar = obtenerReservaPorId(id);
        reservaRepository.delete(reservaAEliminar);
        logger.info("Reserva con id " + id + " eliminada exitosamente.");
        return Boolean.TRUE;
    }

    @Transactional
    public ReservaResponseDTO updateReserva(Long idReserva, ReservaUpdateDTO unaReserva) {
        logger.info("Actualizando reserva con id: " + idReserva);
        Reserva reservaExistente = obtenerReservaPorId(idReserva);

        if (unaReserva.fechaDeInicio().isAfter(unaReserva.fechaDeFinalizacion())) {
            logger.warn("La fecha de inicio de la reserva no puede ser después de la fecha de finalización. Fecha de inicio: " + unaReserva.fechaDeInicio() + ", Fecha de finalización: " + unaReserva.fechaDeFinalizacion());
            throw new ReglaDeNegocioException("La fecha de inicio de la reserva no puede ser después de la fecha de finalización.");
        }
        if (reservaExistente.getVehiculo().getEstadoVehiculo() == EstadoVehiculo.EN_REPARACION) {
            logger.warn("El vehículo con id " + reservaExistente.getVehiculo().getId() + " está en reparación y no puede ser reservado.");
            throw new ReglaDeNegocioException("El vehículo está en reparación y no puede ser reservado.");
        }

        if (reservaRepository.existsOverlappingExcludingSelf(reservaExistente.getVehiculo().getId(), idReserva, unaReserva.fechaDeInicio(), unaReserva.fechaDeFinalizacion())) {
            logger.warn("El vehículo con id " + reservaExistente.getVehiculo().getId() + " ya está reservado para el período indicado.");
            throw new ReglaDeNegocioException("El vehículo ya está reservado para el período indicado.");
        }
        reservaExistente.setFechaDeInicio(unaReserva.fechaDeInicio());
        reservaExistente.setFechaDeFinalizacion(unaReserva.fechaDeFinalizacion());

        logger.info("Reserva con id " + idReserva + " actualizada exitosamente.");
        return ReservaMapper.toResponseDTO(reservaExistente);
    }

    private Reserva obtenerReservaPorId(Long idReserva) {
        return reservaRepository.findById(idReserva).orElseThrow(() -> {
            logger.warn("La reserva con id " + idReserva + " no existe.");
            return new RecursoNoEncontradoException("La reserva con id " + idReserva + " no existe.");
        });
    }
}
