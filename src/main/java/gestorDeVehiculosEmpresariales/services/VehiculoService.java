package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoUpdateDTO;
import gestorDeVehiculosEmpresariales.entities.*;
import gestorDeVehiculosEmpresariales.exceptions.RecursoNoEncontradoException;
import gestorDeVehiculosEmpresariales.exceptions.ReglaDeNegocioException;
import gestorDeVehiculosEmpresariales.mappers.VehiculoMapper;
import gestorDeVehiculosEmpresariales.repositories.DepartamentoRepository;
import gestorDeVehiculosEmpresariales.repositories.NovedadRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehiculoService {
    private static final Logger logger = LoggerFactory.getLogger(VehiculoService.class);
    private final VehiculoRepository vehiculoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final NovedadRepository novedadRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository, DepartamentoRepository departamentoRepository, NovedadRepository novedadRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.departamentoRepository = departamentoRepository;
        this.novedadRepository = novedadRepository;
    }

    @Transactional
    public VehiculoResponseDTO saveVehiculo(VehiculoCreateDTO vehiculoDTO) {
        logger.info("Guardando nuevo vehículo con patente: " + vehiculoDTO.patente());
        if (vehiculoRepository.existsByPatente(vehiculoDTO.patente())) {
            logger.warn("El vehículo con patente " + vehiculoDTO.patente() + " ya existe.");
            throw new ReglaDeNegocioException("El vehículo con Patente " + vehiculoDTO.patente() + " ya existe.");
        }

        if (vehiculoDTO.kmActual() < 0) {
            logger.warn("El kilometraje del vehículo no puede ser negativo. Patente: " + vehiculoDTO.patente() + ", KmActual: " + vehiculoDTO.kmActual());
            throw new ReglaDeNegocioException("El vehiculo no puede tener kilometraje negativo");
        }

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(vehiculoDTO.patente());
        vehiculo.setMarca(vehiculoDTO.marca());
        vehiculo.setModelo(vehiculoDTO.modelo());
        vehiculo.setKmActual(vehiculoDTO.kmActual());
        vehiculo.setFechaDeService(vehiculoDTO.fechaDeService());
        vehiculo.setVencimientoService(vehiculoDTO.vencimientoService());
        vehiculo.setVtvVigente(vehiculoDTO.vtvVigente());
        vehiculo.setVencimientoVtv(vehiculoDTO.vencimientoVtv());
        vehiculo.setNivelCombustible(vehiculoDTO.nivelCombustible());
        vehiculo.setEstadoVehiculo(vehiculoDTO.estadoVehiculo());
        vehiculo.setNumeroTarjetaNafta(vehiculoDTO.numeroTarjetaNafta());

        if (vehiculoDTO.departamentoId() != null) {
            Departamento dto = departamentoRepository.findById(vehiculoDTO.departamentoId()).orElseThrow(() -> {
                logger.warn("El departamento con id " + vehiculoDTO.departamentoId() + " no existe. No se puede asignar al vehículo con patente: " + vehiculoDTO.patente());
                return new RecursoNoEncontradoException("El departamento con id " + vehiculoDTO.departamentoId() + " no existe. No se puede asignar al vehículo.");
            });
            vehiculo.setDepartamento(dto);
        }
        Vehiculo saved = vehiculoRepository.save(vehiculo);
        logger.info("Vehículo guardado exitosamente con id: " + saved.getId());
        return VehiculoMapper.toResponseDTO(saved);
    }

    @Transactional
    public VehiculoResponseDTO updateVehiculo(Long idVehiculo, VehiculoUpdateDTO unVehiculoDTO) {
        logger.info("Actualizando vehículo con id: " + idVehiculo);

        Vehiculo vehiculoExistente = vehiculoRepository.findById(idVehiculo).orElseThrow(() -> {
            logger.warn("El vehículo con id " + idVehiculo + " no existe.");
            return new RecursoNoEncontradoException("El vehículo con id " + idVehiculo + " no existe.");
        });


        if (unVehiculoDTO.kmActual() < vehiculoExistente.getKmActual()) {
            logger.warn("Intento de actualizar el vehículo con id " + idVehiculo + " con un kilometraje menor al registrado anteriormente. Kilometraje registrado: " + vehiculoExistente.getKmActual() + ", Kilometraje nuevo: " + unVehiculoDTO.kmActual());
            throw new ReglaDeNegocioException("El kilometraje actual no puede ser menor al kilometraje registrado anteriormente.");
        }
        vehiculoExistente.setEstadoVehiculo(unVehiculoDTO.estadoVehiculo());
        vehiculoExistente.setKmActual(unVehiculoDTO.kmActual());
        vehiculoExistente.setFechaDeService(unVehiculoDTO.fechaDeService());
        vehiculoExistente.setVencimientoService(unVehiculoDTO.vencimientoService());
        vehiculoExistente.setVtvVigente(unVehiculoDTO.vtvVigente());
        vehiculoExistente.setVencimientoVtv(unVehiculoDTO.vencimientoVtv());
        vehiculoExistente.setNivelCombustible(unVehiculoDTO.nivelCombustible());


        logger.info("Vehículo con id " + idVehiculo + " actualizado exitosamente.");
        return VehiculoMapper.toResponseDTO(vehiculoExistente);

    }

    @Transactional(readOnly = true)
    public List<VehiculoSimpleDTO> findAllVehiculos() {
        logger.info("Obteniendo lista de todos los vehículos.");
        List<Vehiculo> vehiculos = vehiculoRepository.findAll();

        List<VehiculoSimpleDTO> dtos = vehiculos.stream().map(VehiculoMapper::toSimpleDTO).toList();
        logger.info("Se encontraron " + dtos.size() + " vehículos en total.");

        return (dtos);
    }

    @Transactional(readOnly = true)
    public VehiculoResponseDTO findVehiculoById(Long idVehiculo) {
        logger.info("Buscando vehículo con id: " + idVehiculo);
        Vehiculo vehiculoExistente = vehiculoRepository.findById(idVehiculo).orElseThrow(() -> {
            logger.warn("El vehículo con id " + idVehiculo + " no existe.");
            return new RecursoNoEncontradoException("El vehículo con id " + idVehiculo + " no existe.");
        });
        VehiculoResponseDTO dto = VehiculoMapper.toResponseDTO(vehiculoExistente);
        logger.info("Vehículo con id " + idVehiculo + " encontrado exitosamente.");
        return dto;
    }

    @Transactional(readOnly = true)
    public List<VehiculoSimpleDTO> findVehiculosByDepartamentoId(Long idDepartamento) {
        logger.info("Buscando vehículos del departamento con id: " + idDepartamento);
        List<Vehiculo> vehiculos = vehiculoRepository.findByDepartamentoId(idDepartamento);

        List<VehiculoSimpleDTO> dtos = vehiculos.stream().map(VehiculoMapper::toSimpleDTO).toList();
        logger.info("Se encontraron " + vehiculos.size() + " vehículos para el departamento con id: " + idDepartamento);
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<VehiculoSimpleDTO> findVehiculosByEstado(EstadoVehiculo estadoVehiculo) {
        logger.info("Buscando vehículos con estado: " + estadoVehiculo);
        List<Vehiculo> vehiculos = vehiculoRepository.findByEstadoVehiculo(estadoVehiculo);

        List<VehiculoSimpleDTO> dtos = vehiculos.stream().map(VehiculoMapper::toSimpleDTO).toList();
        logger.info("Se encontraron " + vehiculos.size() + " vehículos con estado: " + estadoVehiculo);
        return dtos;
    }

    @Transactional
    public Boolean deleteVehiculoById(Long idVehiculo) {
        logger.info("Eliminando vehículo con id: " + idVehiculo);

        Vehiculo vehiculoAEliminar = vehiculoRepository.findById(idVehiculo).orElseThrow(() -> {
            logger.warn("El vehículo con id " + idVehiculo + " no existe.");
            return new RecursoNoEncontradoException("El vehículo con id " + idVehiculo + " no existe.");
        });

        if (!vehiculoAEliminar.getNovedades().isEmpty()) {
            logger.warn("Intento de eliminar el vehículo con id " + idVehiculo + " que tiene historial de novedades. Cantidad de novedades: " + vehiculoAEliminar.getNovedades().size());
            throw new ReglaDeNegocioException("No se puede eliminar el vehículo con id " + idVehiculo + " porque tiene historial de novedades.");
        }

        vehiculoRepository.deleteById(idVehiculo);
        logger.info("Vehículo con id " + idVehiculo + " eliminado exitosamente.");
        return Boolean.TRUE;
    }

    @Transactional
    public void recalcularEstadoPorNovedades(Vehiculo vehiculo) {
        Long cantNovedadesUrgentes = novedadRepository.countByVehiculoAndUrgenciaAndEstadoNovedadNot(vehiculo, Urgencia.INMEDIATA, EstadoNovedad.SOLUCIONADO);

        if ((cantNovedadesUrgentes > 0 && vehiculo.getEstadoVehiculo().equals(EstadoVehiculo.DISPONIBLE))) {
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
