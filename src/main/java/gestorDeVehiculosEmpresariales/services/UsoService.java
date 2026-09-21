package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.dto.uso.TerminarUsoDTO;
import gestorDeVehiculosEmpresariales.dto.uso.UsoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.uso.UsoResponseDTO;
import gestorDeVehiculosEmpresariales.entities.*;
import gestorDeVehiculosEmpresariales.exceptions.RecursoNoEncontradoException;
import gestorDeVehiculosEmpresariales.exceptions.ReglaDeNegocioException;
import gestorDeVehiculosEmpresariales.mappers.UsoMapper;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.ReservaRepository;
import gestorDeVehiculosEmpresariales.repositories.UsoRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public UsoResponseDTO comenzarUso(UsoCreateDTO unUso) {
        logger.info("Intentando comenzar un nuevo uso para el vehículo con id: " + unUso.idVehiculo() + " por el empleado con id: " + unUso.idEmpleado());

        Vehiculo vehiculoReal = vehiculoRepository.findById(unUso.idVehiculo()).orElseThrow(() -> {
            logger.warn("Vehículo no encontrado con id: " + unUso.idVehiculo());
            return new RecursoNoEncontradoException("Vehículo no encontrado con id: " + unUso.idVehiculo());
        });

        Empleado empleadoReal = empleadoRepository.findById(unUso.idEmpleado()).orElseThrow(() -> {
            logger.warn("Empleado no encontrado con id: " + unUso.idEmpleado());
            return new RecursoNoEncontradoException("Empleado no encontrado con id: " + unUso.idEmpleado());
        });

        if (reservaRepository.existsOverlapping(vehiculoReal.getId(), LocalDateTime.now(), LocalDateTime.now().plusHours(3))) {
            logger.warn("El vehículo con id: " + unUso.idVehiculo() + " tiene una reserva que se superpone con el período de uso solicitado.");
            throw new ReglaDeNegocioException("El vehículo tiene una reserva que se superpone con el período de uso solicitado.");
        }

        if (vehiculoReal.getEstadoVehiculo() != EstadoVehiculo.DISPONIBLE) {
            logger.warn("El vehículo con id: " + unUso.idVehiculo() + " no está disponible para su uso. Estado actual: " + vehiculoReal.getEstadoVehiculo());
            throw new ReglaDeNegocioException("El vehículo no está disponible para su uso.");
        }

        if (!empleadoReal.getTieneRegistroConducir()) {
            logger.warn("El empleado con id: " + empleadoReal.getId() + " no tiene registro de conducir.");
            throw new ReglaDeNegocioException("El empleado no tiene registro de conducir.");
        }

        if (empleadoReal.getVencimientoLicencia() == null) {
            logger.warn("El empleado con id: " + empleadoReal.getId() + " no tiene fecha de vencimiento de licencia registrada.");
            throw new ReglaDeNegocioException("El empleado no tiene fecha de vencimiento de licencia registrada.");
        }

        if (empleadoReal.getVencimientoLicencia().isBefore(LocalDate.now())) {
            logger.warn("La licencia del empleado con id: " + unUso.idEmpleado() + " ha vencido el: " + empleadoReal.getVencimientoLicencia());
            throw new ReglaDeNegocioException("La licencia del empleado ha vencido.");
        }

        if (usoRepository.existsByVehiculoAndFechaFinalizacionIsNull(vehiculoReal)) {
            logger.warn("El vehículo con id: " + unUso.idVehiculo() + " ya está en uso por otro empleado.");
            throw new ReglaDeNegocioException("El vehículo ya está en uso.");
        }

        if (usoRepository.existsByEmpleadoAndFechaFinalizacionIsNull(empleadoReal)) {
            logger.warn("El empleado con id: " + unUso.idEmpleado() + " ya tiene un uso activo.");
            throw new ReglaDeNegocioException("El empleado ya tiene un uso activo.");
        }

        Uso usoNuevo = new Uso();

        usoNuevo.setEmpleado(empleadoReal);
        usoNuevo.setVehiculo(vehiculoReal);
        usoNuevo.setFechaInicio(LocalDateTime.now());
        usoNuevo.getVehiculo().setEstadoVehiculo(EstadoVehiculo.EN_USO);
        Uso saved = usoRepository.save(usoNuevo);
        logger.info("Comenzando un nuevo uso para el vehículo con id: " + unUso.idVehiculo() + " por el empleado con id: " + unUso.idEmpleado());
        return UsoMapper.toResponseDTO(saved);
    }

    @Transactional
    public UsoResponseDTO terminarUso(Long idUso, TerminarUsoDTO usoFinalizarDTO) {

        Integer kmActualizados = usoFinalizarDTO.kilometrajeFinal();
        logger.info("Intentando terminar el uso con id: " + idUso + " y actualizar los kilómetros a: " + kmActualizados);
        Uso uso = this.obtenerUsoById(idUso);

        if (uso.getFechaFinalizacion() != null) {
            logger.warn("El uso con id: " + idUso + " ya ha sido finalizado el: " + uso.getFechaFinalizacion());
            throw new ReglaDeNegocioException("El uso ya ha sido finalizado.");
        }

        if (uso.getVehiculo().getEstadoVehiculo() != EstadoVehiculo.EN_USO) {
            logger.warn("El vehículo con id: " + uso.getVehiculo().getId() + " no está actualmente en uso. Estado actual: " + uso.getVehiculo().getEstadoVehiculo());
            throw new ReglaDeNegocioException("El vehículo no está actualmente en uso.");
        }

        if (uso.getVehiculo().getKmActual() > kmActualizados) {
            logger.warn("Los kilómetros actualizados (" + kmActualizados + ") no pueden ser menores que los kilómetros actuales del vehículo (" + uso.getVehiculo().getKmActual() + ").");
            throw new ReglaDeNegocioException("Los kilómetros actualizados no pueden ser menores que los kilómetros actuales del vehículo.");
        }
        uso.setEstadoDeUso(EstadoUso.FINALIZADO);
        uso.setFechaFinalizacion(LocalDateTime.now());
        uso.getVehiculo().setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
        uso.getVehiculo().setKmActual(kmActualizados);

        logger.info("Uso con id: " + idUso + " finalizado exitosamente. Vehículo con id: " + uso.getVehiculo().getId() + " ahora disponible con kilómetros actualizados a: " + kmActualizados);

        return UsoMapper.toResponseDTO(uso);
    }

    @Transactional(readOnly = true)
    public UsoResponseDTO findUsoById(Long idUso) {
        logger.info("Buscando uso con id: " + idUso);
        Uso uso = this.obtenerUsoById(idUso);
        return UsoMapper.toResponseDTO(uso);
    }

    @Transactional(readOnly = true)
    public List<UsoResponseDTO> findAllUso() {
        logger.info("Buscando todos los usos registrados");
        List<Uso> usos = usoRepository.findAll();
        return usos.stream().map(UsoMapper::toResponseDTO).toList();
    }

    @Transactional
    public Boolean deleteUso(Long idUso) {
        logger.info("Intentando eliminar el uso con id: " + idUso);
        Uso uso = this.obtenerUsoById(idUso);
        this.usoRepository.delete(uso);
        logger.info("Uso con id: " + idUso + " eliminado exitosamente.");
        return Boolean.TRUE;
    }

    private Uso obtenerUsoById(Long idUso) {
        logger.info("Buscando uso con id: " + idUso);

        Uso uso = usoRepository.findById(idUso).orElseThrow(() -> {
            logger.warn("Uso no encontrado con id: " + idUso);
            return new RecursoNoEncontradoException("Uso no encontrado con id: " + idUso);
        });
        return uso;
    }

}
