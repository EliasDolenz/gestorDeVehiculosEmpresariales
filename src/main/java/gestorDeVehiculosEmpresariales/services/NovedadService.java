package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.dto.novedad.NovedadCreateDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadResponseDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadUpdateDTO;
import gestorDeVehiculosEmpresariales.entities.*;
import gestorDeVehiculosEmpresariales.exceptions.RecursoNoEncontradoException;
import gestorDeVehiculosEmpresariales.mappers.NovedadMapper;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.NovedadRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NovedadService {
    private static final Logger logger = LoggerFactory.getLogger(NovedadService.class);
    private final NovedadRepository novedadRepository;
    private final VehiculoRepository vehiculoRepository;
    private final EmpleadoRepository empleadoRepository;

    public NovedadService(NovedadRepository novedadRepository, EmpleadoRepository empleadoRepository, VehiculoRepository vehiculoRepository) {
        this.novedadRepository = novedadRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public NovedadResponseDTO saveNovedad(NovedadCreateDTO unaNovedad) {
        logger.info("Guardando nueva novedad para el vehículo con id: " + unaNovedad.idVehiculo());

        Vehiculo vehiculo = vehiculoRepository.findById(unaNovedad.idVehiculo()).orElseThrow(() -> {
            logger.warn("No se encontro el vehiculo con el ID: " + unaNovedad.idVehiculo());
            return new RecursoNoEncontradoException("El vehiculo que indica la novedad no se encuentra en el sistema");
        });

        Empleado empleado = empleadoRepository.findById(unaNovedad.idEmpleado()).orElseThrow(() -> {
            logger.warn("No se encontro el empleado con el ID: " + unaNovedad.idEmpleado());
            return new RecursoNoEncontradoException("El empleado que indica la novedad no se encuentra en el sistema");
        });


        Novedad novedad = new Novedad();
        novedad.setDescripcion(unaNovedad.descripcion());
        novedad.setVehiculo(vehiculo);
        novedad.setEmpleado(empleado);
        novedad.setFechaReporte(LocalDateTime.now());
        novedad.setEstadoNovedad(unaNovedad.estadoNovedad());
        novedad.setUrgencia(unaNovedad.urgencia());


        Novedad novedadGuardada = this.novedadRepository.save(novedad);
        this.actualizarEstadoVehiculoSiEsNecesario(vehiculo);
        NovedadResponseDTO novedadDTO = NovedadMapper.toResponseDTO(novedadGuardada);

        logger.info("Guardando novedad con descripción: " + unaNovedad.descripcion() + " para el vehículo con id: " + unaNovedad.idVehiculo() + " reportada por el empleado con id: " + unaNovedad.idEmpleado());
        return novedadDTO;
    }

    @Transactional(readOnly = true)
    public List<NovedadSimpleDTO> getNovedades() {
        logger.info("Obteniendo todas las novedades");

        List<Novedad> novedades = this.novedadRepository.findAll();

        List<NovedadSimpleDTO> novedadesDTO = novedades.stream().map(NovedadMapper::toSimpleDTO).toList();
        logger.info("Se encontraron " + novedadesDTO.size() + " novedades");

        return novedadesDTO;
    }

    @Transactional(readOnly = true)
    public NovedadResponseDTO getNovedadById(Long idNovedad) {
        logger.info("Obteniendo novedad con id: " + idNovedad);

        Novedad novedad = this.obtenerNovedadPorId(idNovedad);

        logger.info("Novedad encontrada: " + novedad);

        return NovedadMapper.toResponseDTO(novedad);

    }

    @Transactional
    public NovedadResponseDTO updateNovedad(Long idNovedad, NovedadUpdateDTO unaNovedad) {
        logger.info("Actualizando novedad con id: " + idNovedad);
        Novedad novedadExistente = this.obtenerNovedadPorId(idNovedad);


        novedadExistente.setEstadoNovedad(unaNovedad.estadoNovedad());
        novedadExistente.setDescripcion(unaNovedad.descripcion());
        novedadExistente.setUrgencia(unaNovedad.urgencia());

        this.actualizarEstadoVehiculoSiEsNecesario(novedadExistente.getVehiculo());

        logger.info("Novedad con id: " + idNovedad + " actualizada exitosamente. Nueva descripción: " + unaNovedad.descripcion() + ", Nuevo estado: " + unaNovedad.estadoNovedad());
        return NovedadMapper.toResponseDTO(novedadExistente);
    }

    @Transactional
    public Boolean deleteNovedad(Long idNovedad) {
        logger.info("Eliminando novedad con id: " + idNovedad);
        Novedad novedad = this.obtenerNovedadPorId(idNovedad);


        logger.info("Se eliminó la novedad con id: " + idNovedad + " y descripción: " + novedad.getDescripcion());

        novedadRepository.deleteById(idNovedad);
        this.actualizarEstadoVehiculoSiEsNecesario(novedad.getVehiculo());

        return Boolean.TRUE;
    }

    private Novedad obtenerNovedadPorId(Long idNovedad) {
        return novedadRepository.findById(idNovedad).orElseThrow(() -> {
            logger.warn("No se encontró la novedad con id: " + idNovedad);
            return new RecursoNoEncontradoException("La novedad con id " + idNovedad + " no existe.");
        });
    }


    private void actualizarEstadoVehiculoSiEsNecesario(Vehiculo vehiculo) {

        //Este método tengo q verificarlo, porque afecta el estado del vehiculo, y va a tener problemas cuando se reporte una novedad de urgencia inmediata y el vehiculo este En_Uso

        Long cantNovedadesUrgentes = novedadRepository.countByVehiculoAndUrgencia(vehiculo, Urgencia.INMEDIATA);

        if ((cantNovedadesUrgentes > 0 && !vehiculo.getEstadoVehiculo().equals(EstadoVehiculo.EN_REPARACION))) {
            vehiculo.setEstadoVehiculo(EstadoVehiculo.EN_REPARACION);
            logger.info("Actualizando estado del vehículo con id: " + vehiculo.getId() + " a EN_REPARACION ya que tiene novedades urgentes");
            vehiculoRepository.save(vehiculo);
        } else if ((cantNovedadesUrgentes == 0 && vehiculo.getEstadoVehiculo().equals(EstadoVehiculo.EN_REPARACION))) {
            vehiculo.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
            logger.info("Actualizando estado del vehículo con id: " + vehiculo.getId() + " a DISPONIBLE ya que no tiene más novedades urgentes");
            vehiculoRepository.save(vehiculo);
        }

    }

}
