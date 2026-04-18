package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.Empleado;
import gestorDeVehiculosEmpresariales.entities.Novedad;
import gestorDeVehiculosEmpresariales.entities.Urgencia;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.NovedadRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Novedad saveNovedad(Novedad unaNovedad) {
        logger.info("Guardando nueva novedad para el vehículo con id: " + unaNovedad.getVehiculo().getId());
        Vehiculo vehiculo = vehiculoRepository.findById(unaNovedad.getVehiculo().getId()).orElseThrow(() -> {
            logger.warn("No se encontro el vehiculo con el ID: " + unaNovedad.getVehiculo().getId());
            return
                    new IllegalArgumentException("El vehiculo que indica la novedad no se encuentra en el sistema");
        });

        Empleado empleado = empleadoRepository.findById(unaNovedad.getEmpleado().getId()).orElseThrow(() -> {
            logger.warn("No se encontro el empleado con el ID: " + unaNovedad.getEmpleado().getId());
            return
                    new IllegalArgumentException("El empleado que indica la novedad no se encuentra en el sistema");
        });

        if (unaNovedad.getUrgencia() == Urgencia.INMEDIATA) {
            vehiculo.setEstadoVehiculo(gestorDeVehiculosEmpresariales.entities.EstadoVehiculo.EN_REPARACION);
        }

        logger.info("Guardando novedad con descripción: " + unaNovedad.getDescripcion() + " para el vehículo con id: " + unaNovedad.getVehiculo().getId() + " reportada por el empleado con id: " + unaNovedad.getEmpleado().getId());
        return this.novedadRepository.save(unaNovedad);
    }

    @Transactional
    public List<Novedad> getNovedades() {
        logger.info("Obteniendo todas las novedades");
        return this.novedadRepository.findAll();
    }

    @Transactional
    public Novedad getNovedadById(Long idNovedad) {
        logger.info("Obteniendo novedad con id: " + idNovedad);
        return this.novedadRepository.findById(idNovedad).orElseThrow(() -> {
            logger.warn("No se encontró la novedad con id: " + idNovedad);
            throw new IllegalArgumentException("La novedad con id " + idNovedad + " no existe.");
        });
    }

    @Transactional
    public Novedad updateNovedad(Long idNovedad, Novedad unaNovedad) {
        logger.info("Actualizando novedad con id: " + idNovedad);
        Novedad novedadExistente = novedadRepository.findById(idNovedad).orElseThrow(() -> {
            logger.warn("No se encontró la novedad con id: " + idNovedad);
            throw new IllegalArgumentException("La novedad con id " + idNovedad + " no existe.");
        });

        if (!novedadExistente.getEmpleado().getId().equals(unaNovedad.getEmpleado().getId())) {
            logger.warn("Intento de cambiar el empleado que reportó la novedad. Empleado actual: " + novedadExistente.getEmpleado().getId() + ", Empleado nuevo: " + unaNovedad.getEmpleado().getId());
            throw new IllegalArgumentException("No se puede cambiar el empleado que reportó la novedad.");
        }

        if (!novedadExistente.getVehiculo().getId().equals(unaNovedad.getVehiculo().getId())) {
            logger.warn("Intento de cambiar el vehículo al que pertenece la novedad. Vehículo actual: " + novedadExistente.getVehiculo().getId() + ", Vehículo nuevo: " + unaNovedad.getVehiculo().getId());
            throw new IllegalArgumentException("No se puede cambiar el vehiculo al que pertenece la novedad.");
        }


        novedadExistente.setEstadoNovedad(unaNovedad.getEstadoNovedad());
        novedadExistente.setDescripcion(unaNovedad.getDescripcion());
        novedadRepository.save(novedadExistente);
        logger.info("Novedad con id: " + idNovedad + " actualizada exitosamente. Nueva descripción: " + unaNovedad.getDescripcion() + ", Nuevo estado: " + unaNovedad.getEstadoNovedad());
        return novedadExistente;
    }

    @Transactional
    public Boolean deleteNovedad(Long idNovedad) {
        logger.info("Eliminando novedad con id: " + idNovedad);
        Novedad novedad = novedadRepository.findById(idNovedad).orElseThrow(() -> {
            logger.warn("No se encontró la novedad con id: " + idNovedad);
            throw new IllegalArgumentException("La novedad con id " + idNovedad + " no existe.");
        });
        if (!novedadRepository.existsById(idNovedad)) {
            logger.info("No se encontró la novedad con id: " + idNovedad + " para eliminar");
            throw new IllegalArgumentException("La novedad con id " + idNovedad + " no existe.");
        }

        Long cantNovedadesUrgentes = novedadRepository.countByVehiculoAndUrgencia(novedad.getVehiculo(), Urgencia.INMEDIATA);
        logger.info("Cantidad de novedades urgentes para el vehículo con id: " + novedad.getVehiculo().getId() + " es: " + cantNovedadesUrgentes);
        novedadRepository.deleteById(idNovedad);


        if (cantNovedadesUrgentes == 1 && novedad.getUrgencia() == (Urgencia.INMEDIATA)) {
            vehiculoRepository.findById(novedad.getVehiculo().getId()).ifPresent(vehiculo -> {
                ;
                vehiculo.setEstadoVehiculo(gestorDeVehiculosEmpresariales.entities.EstadoVehiculo.DISPONIBLE);
                logger.info("Actualizando estado del vehículo con id: " + vehiculo.getId() + " a DISPONIBLE ya que se eliminó la última novedad urgente");
                vehiculoRepository.save(vehiculo);
            });
        }

        return Boolean.TRUE;
    }

}
