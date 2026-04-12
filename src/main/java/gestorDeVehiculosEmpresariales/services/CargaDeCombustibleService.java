package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.CargaDeCombustible;
import gestorDeVehiculosEmpresariales.entities.Combustible;
import gestorDeVehiculosEmpresariales.entities.Empleado;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import gestorDeVehiculosEmpresariales.repositories.CargaDeCombustibleRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargaDeCombustibleService {
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
        return cargaDeCombustibleRepository.findById(idCarga).orElseThrow(() -> new RuntimeException("No se encontró la carga de combustible con id: " + idCarga));
    }

    @Transactional
    public List<CargaDeCombustible> findAllCargas() {
        return cargaDeCombustibleRepository.findAll();
    }

    @Transactional
    public CargaDeCombustible saveCarga(CargaDeCombustible cargaDeCombustible) {

        Vehiculo vehiculoReal = vehiculoRepository.findById(cargaDeCombustible.getVehiculo().getId()).orElseThrow(() -> new RuntimeException("No se encontró el vehículo con id: " + cargaDeCombustible.getVehiculo().getId()));

        Empleado empleadoReal = empleadoRepository.findById(cargaDeCombustible.getEmpleado().getId()).orElseThrow(() -> new RuntimeException("No se encontró el vehículo con id: " + cargaDeCombustible.getEmpleado().getId()));

        if (cargaDeCombustible.getKmVehiculo() <= vehiculoReal.getKmActual()) {
            throw new RuntimeException("El kilometraje de carga debe ser mayor al actual (" + vehiculoReal.getKmActual() + ")");
        }
        vehiculoReal.setKmActual(cargaDeCombustible.getKmVehiculo());
        vehiculoReal.setNivelCombustible(Combustible.LLENO);
        cargaDeCombustible.setVehiculo(vehiculoReal);
        cargaDeCombustible.setEmpleado(empleadoReal);
        cargaDeCombustible.setFechaRecarga(java.time.LocalDateTime.now());

        return cargaDeCombustibleRepository.save(cargaDeCombustible);

    }

    @Transactional
    public CargaDeCombustible updateCarga(Long idCarga, CargaDeCombustible carga) {
        CargaDeCombustible cargaExistente = this.findCargaById(idCarga);

        Vehiculo vehiculoReal = vehiculoRepository.findById(carga.getVehiculo().getId()).orElseThrow(() -> new RuntimeException("No se encontró el vehículo con id: " + carga.getVehiculo().getId()));


        Empleado empleadoReal = empleadoRepository.findById(carga.getEmpleado().getId()).orElseThrow(() -> new RuntimeException("No se encontró el vehículo con id: " + carga.getEmpleado().getId()));

        if (carga.getVehiculo() == null || carga.getEmpleado() == null) {
            throw new RuntimeException("El vehículo y el empleado no pueden ser nulos");
        }


        if (carga.getKmVehiculo() < vehiculoReal.getKmActual()) {
            throw new RuntimeException("El kilometraje del vehículo a la hora  de cargar no puede ser menor al kilometraje actual del vehículo");
        }

        vehiculoReal.setKmActual(carga.getKmVehiculo());

        cargaExistente.setVehiculo(vehiculoReal);
        cargaExistente.setCantidadLitros(carga.getCantidadLitros());
        cargaExistente.setKmVehiculo(carga.getKmVehiculo());
        return cargaDeCombustibleRepository.save(cargaExistente);

    }
}
