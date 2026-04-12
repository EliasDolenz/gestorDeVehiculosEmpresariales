package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import gestorDeVehiculosEmpresariales.entities.Uso;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.UsoRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;

@Service
public class UsoService {
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
        if (unUso.getVehiculo().getEstadoVehiculo() != EstadoVehiculo.DISPONIBLE) {
            throw new IllegalStateException("El vehículo no está disponible para su uso.");
        }

        if (!unUso.getEmpleado().getTieneRegistroConducir()) {
            throw new IllegalStateException("El empleado no tiene registro de conducir.");
        }

        if (unUso.getEmpleado().getVencimientoLicencia().isBefore(ChronoLocalDate.from(LocalDateTime.now()))) {
            throw new IllegalStateException("La licencia del empleado ha vencido.");
        }

        if (usoRepository.existsByVehiculoAndFechaFinalizacionIsNull(unUso.getVehiculo())) {
            throw new IllegalStateException("El vehículo ya está en uso.");
        }

        if (usoRepository.existsByEmpleadoAndFechaFinalizacionIsNull(unUso.getEmpleado())) {
            throw new IllegalStateException("El empleado ya tiene un uso activo.");
        }

        return usoRepository.save(unUso);
    }

    @Transactional
    public Boolean terminarUso(Long idUso, Integer kmActualizados) {
        Uso uso = usoRepository.findById(idUso).orElseThrow(() -> new IllegalStateException("Uso no encontrado con id: " + idUso));

        if (uso.getFechaFinalizacion() != null) {
            throw new IllegalStateException("El uso ya ha sido finalizado.");
        }

        if (uso.getVehiculo().getEstadoVehiculo() != EstadoVehiculo.EN_USO) {
            throw new IllegalStateException("El vehículo no está actualmente en uso.");
        }

        if (uso.getVehiculo().getKmActual() > kmActualizados) {
            throw new IllegalStateException("Los kilómetros actualizados no pueden ser menores que los kilómetros actuales del vehículo.");
        }

        uso.setFechaFinalizacion(LocalDateTime.now());
        uso.getVehiculo().setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
        uso.getVehiculo().setKmActual(kmActualizados);
        usoRepository.save(uso);
        return Boolean.TRUE;
    }

    @Transactional
    public Uso findUsoById(Long idUso) {
        return usoRepository.findById(idUso).orElseThrow(() -> new IllegalStateException("Uso no encontrado con id: " + idUso));
    }

    @Transactional
    public List<Uso> findAllUso() {
        return usoRepository.findAll();
    }
}
