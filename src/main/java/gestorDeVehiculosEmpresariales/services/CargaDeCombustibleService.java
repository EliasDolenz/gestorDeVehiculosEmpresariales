package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleCreateDTO;
import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleResponseDTO;
import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleUpdateDTO;
import gestorDeVehiculosEmpresariales.entities.*;
import gestorDeVehiculosEmpresariales.exceptions.RecursoNoEncontradoException;
import gestorDeVehiculosEmpresariales.exceptions.ReglaDeNegocioException;
import gestorDeVehiculosEmpresariales.mappers.CargaDeCombustibleMapper;
import gestorDeVehiculosEmpresariales.repositories.CargaDeCombustibleRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CargaDeCombustibleService {
    private static final Logger logger = LoggerFactory.getLogger(CargaDeCombustibleService.class);

    private final VehiculoRepository vehiculoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final CargaDeCombustibleRepository cargaDeCombustibleRepository;

    public CargaDeCombustibleService(VehiculoRepository vehiculoRepository, EmpleadoRepository empleadoRepository, CargaDeCombustibleRepository cargaDeCombustibleRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.empleadoRepository = empleadoRepository;
        this.cargaDeCombustibleRepository = cargaDeCombustibleRepository;
    }

    @Transactional(readOnly = true)
    public CargaDeCombustibleResponseDTO findCargaById(Long idCarga) {
        logger.info("Buscando carga de combustible con id: " + idCarga);

        CargaDeCombustible cargaDeCombustible = this.obtenerCargaDeCombustiblePorId(idCarga);

        logger.info("Carga de combustible encontrada con id: " + idCarga);
        return CargaDeCombustibleMapper.toResponseDTO(cargaDeCombustible);
    }

    @Transactional(readOnly = true)
    public List<CargaDeCombustibleSimpleDTO> findAllCargas() {
        logger.info("Buscando todas las cargas de combustible");

        List<CargaDeCombustible> cargas = cargaDeCombustibleRepository.findAll();
        logger.info("Se encontraron " + cargas.size() + " cargas de combustible");

        return cargas.stream().map(CargaDeCombustibleMapper::toSimpleDTO).toList();
    }

    @Transactional
    public CargaDeCombustibleResponseDTO saveCarga(CargaDeCombustibleCreateDTO cargaDeCombustible) {
        logger.info("Guardando nueva carga de combustible para el vehículo con id: " + cargaDeCombustible.idVehiculo());

        Vehiculo vehiculoReal = vehiculoRepository.findById(cargaDeCombustible.idVehiculo()).orElseThrow(() -> {
            logger.warn("No se encontro el vehiculo con el ID: " + cargaDeCombustible.idVehiculo());
            return new RecursoNoEncontradoException("No se encontró el vehículo con id: " + cargaDeCombustible.idVehiculo());
        });

        Empleado empleadoReal = empleadoRepository.findById(cargaDeCombustible.idEmpleado()).orElseThrow(() -> {
            logger.warn("No se encontro el empleado con el ID: " + cargaDeCombustible.idEmpleado());
            return new RecursoNoEncontradoException("No se encontró el empleado con id: " + cargaDeCombustible.idEmpleado());
        });

        if (vehiculoReal.getEstadoVehiculo() == EstadoVehiculo.EN_REPARACION) {
            logger.warn("No se puede cargar combustible a un vehículo que se encuentra en reparación");
            throw new ReglaDeNegocioException("No se puede cargar combustible a un vehículo que se encuentra en reparación");
        }

        if (cargaDeCombustible.kmVehiculo() <= vehiculoReal.getKmActual()) {
            logger.warn("El kilometraje de carga (" + cargaDeCombustible.kmVehiculo() + ") debe ser mayor al actual (" + vehiculoReal.getKmActual() + ")");
            throw new ReglaDeNegocioException("El kilometraje de carga debe ser mayor al actual (" + vehiculoReal.getKmActual() + ")");
        }


        vehiculoReal.setKmActual(cargaDeCombustible.kmVehiculo());
        vehiculoReal.setNivelCombustible(Combustible.LLENO);
        vehiculoRepository.save(vehiculoReal);
        CargaDeCombustible nuevaCarga = new CargaDeCombustible();
        nuevaCarga.setCantidadLitros(cargaDeCombustible.cantidadLitros());
        nuevaCarga.setKmVehiculo(cargaDeCombustible.kmVehiculo());
        nuevaCarga.setVehiculo(vehiculoReal);
        nuevaCarga.setEmpleado(empleadoReal);
        nuevaCarga.setFechaRecarga(cargaDeCombustible.fechaRecarga());

        CargaDeCombustible saved = this.cargaDeCombustibleRepository.save(nuevaCarga);
        logger.info("Guardado Exitoso - ID:{}", nuevaCarga.getId());
        return CargaDeCombustibleMapper.toResponseDTO(saved);

    }

    @Transactional
    public CargaDeCombustibleResponseDTO updateCarga(Long idCarga, CargaDeCombustibleUpdateDTO carga) {
        logger.info("Actualizando carga de combustible con id: " + idCarga);
        CargaDeCombustible cargaExistente = this.obtenerCargaDeCombustiblePorId(idCarga);

        Vehiculo vehiculoReal = vehiculoRepository.findById(cargaExistente.getVehiculo().getId()).orElseThrow(() -> {
            logger.warn("No se encontró el vehículo con id: " + cargaExistente.getVehiculo().getId());
            return new RecursoNoEncontradoException("No se encontró el vehículo con id: " + cargaExistente.getVehiculo().getId());
        });

        if (vehiculoReal.getEstadoVehiculo() != EstadoVehiculo.EN_USO) {
            logger.warn("No se puede modificar la carga de combustible de un vehículo que no se encuentra en uso");
            throw new ReglaDeNegocioException("No se puede modificar la carga de combustible de un vehículo que no se encuentra en uso");
        }

        if (carga.kmVehiculo() < vehiculoReal.getKmActual()) {
            logger.warn("El kilometraje del vehículo a la hora de cargar (" + carga.kmVehiculo() + ") no puede ser menor al kilometraje actual del vehículo (" + vehiculoReal.getKmActual() + ")");
            throw new ReglaDeNegocioException("El kilometraje del vehículo a la hora  de cargar no puede ser menor al kilometraje actual del vehículo");
        }



        cargaExistente.setCantidadLitros(carga.cantidadLitros());
        cargaExistente.setKmVehiculo(carga.kmVehiculo());
        cargaExistente.setFechaRecarga(carga.fechaRecarga());


        logger.info("Carga de combustible actualizada exitosamente con id: " + cargaExistente.getId());
        return CargaDeCombustibleMapper.toResponseDTO(cargaExistente);
    }

    @Transactional
    public Boolean deleteCargaById(Long idCarga) {
        logger.info("Eliminando carga de combustible con id: " + idCarga);

        if (!cargaDeCombustibleRepository.existsById(idCarga)) {
            logger.warn("No se encontró la carga de combustible con id: " + idCarga);
            throw new RecursoNoEncontradoException("No se encontró la carga de combustible con id: " + idCarga);
        }
        cargaDeCombustibleRepository.deleteById(idCarga);
        logger.info("Carga de combustible con id: " + idCarga + " eliminada exitosamente.");
        return Boolean.TRUE;
    }

    private CargaDeCombustible obtenerCargaDeCombustiblePorId(Long idCarga) {
        return cargaDeCombustibleRepository.findById(idCarga).orElseThrow(() -> {
            logger.warn("No se encontró la carga de combustible con id: " + idCarga);
            return new RecursoNoEncontradoException("No se encontró la carga de combustible con id: " + idCarga);
        });
    }
}
