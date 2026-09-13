package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoUpdateDTO;
import gestorDeVehiculosEmpresariales.entities.Departamento;
import gestorDeVehiculosEmpresariales.entities.Empresa;
import gestorDeVehiculosEmpresariales.exceptions.RecursoNoEncontradoException;
import gestorDeVehiculosEmpresariales.exceptions.ReglaDeNegocioException;
import gestorDeVehiculosEmpresariales.mappers.DepartamentoMapper;
import gestorDeVehiculosEmpresariales.repositories.DepartamentoRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpresaRepository;
import gestorDeVehiculosEmpresariales.repositories.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class DepartamentoService {
    private static final Logger logger = LoggerFactory.getLogger(DepartamentoService.class);
    private final DepartamentoRepository departamentoRepository;
    private final EmpresaRepository empresaRepository;
    private final EmpleadoRepository empleadoRepository;
    private final VehiculoRepository vehiculoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository, EmpresaRepository empresaRepository, EmpleadoRepository empleadoRepository, VehiculoRepository vehiculoRepository) {
        this.departamentoRepository = departamentoRepository;
        this.empresaRepository = empresaRepository;
        this.empleadoRepository = empleadoRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional
    public DepartamentoResponseDTO saveDepartamento(DepartamentoCreateDTO unDepartamento) {
        logger.info("Guardando nuevo departamento con nombre: " + unDepartamento.nombre());

        Empresa empresa = empresaRepository.findById(unDepartamento.empresaId()).orElseThrow(() -> {
            logger.warn("La empresa con ID " + unDepartamento.empresaId() + " no existe");
            return new RecursoNoEncontradoException("La empresa con ID " + unDepartamento.empresaId() + " no existe");
        });

        Departamento departamentoNuevo = new Departamento();
        departamentoNuevo.setNombre(unDepartamento.nombre());
        departamentoNuevo.setEmpresa(empresa);

        Departamento saved = this.departamentoRepository.save(departamentoNuevo);

        DepartamentoResponseDTO departamentoDTO = DepartamentoMapper.toResponseDTO(saved);
        logger.info("Departamento guardado con ID: " + saved.getId());
        return departamentoDTO;
    }

    @Transactional
    public Boolean deleteDepartamento(Long idDepartamento) {
        logger.info("Eliminando el departamento con ID: " + idDepartamento);

        if (!departamentoRepository.existsById(idDepartamento)) {
            logger.warn("El departamento con ID " + idDepartamento + " no existe");
            throw new RecursoNoEncontradoException("El departamento con ID " + idDepartamento + " no existe");
        }

        if (empleadoRepository.countByDepartamentoId(idDepartamento) > 0) {
            logger.warn("No se puede eliminar el departamento con ID " + idDepartamento + " porque hay empleados asignados a él");
            throw new ReglaDeNegocioException("No se puede eliminar el Departamento porque hay empleados en el mismo");
        }

        if (vehiculoRepository.countByDepartamentoId(idDepartamento) > 0) {
            logger.warn("No se puede eliminar el departamento con ID " + idDepartamento + " porque hay vehículos asignados a él");
            throw new ReglaDeNegocioException("No se puede eliminar el Departamento porque hay vehículos en el mismo");
        }

        departamentoRepository.deleteById(idDepartamento);
        logger.info("Departamento con ID " + idDepartamento + " eliminado exitosamente");
        return Boolean.TRUE;
    }

    @Transactional(readOnly = true)
    public DepartamentoResponseDTO findDepartamentoById(Long idDepartamento) {
        logger.info("Buscando el departamento con ID: " + idDepartamento);
        Departamento departamento = this.obtenerDepartamentoPorId(idDepartamento);
        logger.info("Departamento con ID " + idDepartamento + " encontrado exitosamente");

        return DepartamentoMapper.toResponseDTO(departamento);
    }

    @Transactional(readOnly = true)
    public List<DepartamentoSimpleDTO> findAllDepartamentos() {
        logger.info("Obteniendo todos los departamentos");
        List<Departamento> departamentos = this.departamentoRepository.findAll();
        logger.info("Total de departamentos encontrados: " + departamentos.size());
        return departamentos.stream().map(DepartamentoMapper::toSimpleDTO).toList();
    }

    @Transactional
    public DepartamentoResponseDTO updateDepartamento(Long idDepartamento, DepartamentoUpdateDTO unDepartamento) {
        logger.info("Actualizando el departamento con ID: " + idDepartamento);

        Departamento departamentoExistente = this.obtenerDepartamentoPorId(idDepartamento);
        departamentoExistente.setNombre(unDepartamento.nombre());


        logger.info("Departamento con ID " + idDepartamento + " actualizado exitosamente");
        return DepartamentoMapper.toResponseDTO(departamentoExistente);
    }

    private Departamento obtenerDepartamentoPorId(Long idDepartamento) {
        return departamentoRepository.findById(idDepartamento).orElseThrow(() -> {
            logger.warn("El departamento con ID " + idDepartamento + " no existe");
            return new RecursoNoEncontradoException("El departamento con ID " + idDepartamento + " no existe");
        });
    }
}
