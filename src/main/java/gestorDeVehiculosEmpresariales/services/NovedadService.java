package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.Empleado;
import gestorDeVehiculosEmpresariales.entities.Novedad;
import gestorDeVehiculosEmpresariales.entities.Urgencia;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.NovedadRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NovedadService {
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

        Vehiculo vehiculo = vehiculoRepository.findById(unaNovedad.getVehiculo().getId()).orElseThrow(() -> new IllegalArgumentException("El vehiculo que indica la novedad no se encuentra en el sistema"));

        Empleado empleado = empleadoRepository.findById(unaNovedad.getEmpleado().getId()).orElseThrow(() -> new IllegalArgumentException("El empleado que indica la novedad no se encuentra en el sistema"));

        if (unaNovedad.getUrgencia() == Urgencia.INMEDIATA) {
            vehiculo.setEstadoVehiculo(gestorDeVehiculosEmpresariales.entities.EstadoVehiculo.EN_REPARACION);
        }
        return this.novedadRepository.save(unaNovedad);
    }


    private List<Novedad> getNovedades() {
        return this.novedadRepository.findAll();
    }

    @Transactional
    public Novedad updateNovedad(Long idNovedad, Novedad unaNovedad) {
        Novedad novedadExistente = novedadRepository.findById(idNovedad).orElseThrow(() -> new IllegalArgumentException("La novedad con id " + idNovedad + " no existe."));

        if (!novedadExistente.getEmpleado().getId().equals(unaNovedad.getEmpleado().getId())) {
            throw new IllegalArgumentException("No se puede cambiar el empleado que reportó la novedad.");
        }

        if (!novedadExistente.getVehiculo().getId().equals(unaNovedad.getVehiculo().getId())) {
            throw new IllegalArgumentException("No se puede cambiar el vehiculo al que pertenece la novedad.");
        }


        novedadExistente.setEstadoNovedad(unaNovedad.getEstadoNovedad());
        novedadExistente.setDescripcion(unaNovedad.getDescripcion());
        return novedadRepository.save(novedadExistente);
    }

    @Transactional
    public Boolean deleteNovedad(Long idNovedad) {
        Novedad novedad = novedadRepository.findById(idNovedad).orElseThrow(() -> new IllegalArgumentException("La novedad con id " + idNovedad + " no existe."));
        if (!novedadRepository.existsById(idNovedad)) {
            throw new IllegalArgumentException("La novedad con id " + idNovedad + " no existe.");
        }

        Long cantNovedadesUrgentes = novedadRepository.countByVehiculoAndUrgencia(novedad.getVehiculo(), Urgencia.INMEDIATA);

        novedadRepository.deleteById(idNovedad);


        if (cantNovedadesUrgentes == 1 && novedad.getUrgencia() == (Urgencia.INMEDIATA)) {
            vehiculoRepository.findById(novedad.getVehiculo().getId()).ifPresent(vehiculo -> {
                ;
                vehiculo.setEstadoVehiculo(gestorDeVehiculosEmpresariales.entities.EstadoVehiculo.DISPONIBLE);
                vehiculoRepository.save(vehiculo);
            });
        }
        return Boolean.TRUE;
    }

}
