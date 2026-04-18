package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.Departamento;
import gestorDeVehiculosEmpresariales.entities.Empresa;
import gestorDeVehiculosEmpresariales.repositories.DepartamentoRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpresaRepository;
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

    public DepartamentoService(DepartamentoRepository departamentoRepository, EmpresaRepository empresaRepository, EmpleadoRepository empleadoRepository) {
        this.departamentoRepository = departamentoRepository;
        this.empresaRepository = empresaRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public Departamento saveDepartamento(Departamento unDepartamento) {
        logger.info("Guardando nuevo departamento con nombre: " + unDepartamento.getNombre());

        Empresa empresa = empresaRepository.findById(unDepartamento.getEmpresa().getId()).orElseThrow(() -> {
            logger.warn("La empresa con ID " + unDepartamento.getEmpresa().getId() + " no existe");
            throw new IllegalArgumentException("La empresa con ID " + unDepartamento.getEmpresa().getId() + " no existe");
        });

        unDepartamento.setEmpresa(empresa);
        Departamento saved = this.departamentoRepository.save(unDepartamento);
        logger.info("Departamento guardado con ID: " + saved.getId());
        return saved;
    }

    @Transactional
    public Boolean deleteDepartamento(Long idDepartamento) {
        logger.info("Eliminanando el departamento con ID: " + idDepartamento);

        if (!departamentoRepository.existsById(idDepartamento)) {
            logger.warn("El departamento con ID " + idDepartamento + " no existe");
            throw new IllegalArgumentException("El departamento con ID " + idDepartamento + " no existe");
        }

        if (empleadoRepository.countByDepartamentoId(idDepartamento) > 0) {
            logger.warn("No se puede eliminar el departamento con ID " + idDepartamento + " porque hay empleados asignados a él");
            throw new IllegalArgumentException("No se puede eliminar el Departamento porque hay empleados en el mismo");
        }

        departamentoRepository.deleteById(idDepartamento);
        logger.info("Departamento con ID " + idDepartamento + " eliminado exitosamente");
        return Boolean.TRUE;
    }

    @Transactional
    public Departamento findDepartamentoById(Long idDepartamento) {
        logger.info("Buscando el departametno con ID: " + idDepartamento);
        Departamento departamento = this.departamentoRepository.findById(idDepartamento).orElseThrow(() -> {
            logger.warn("El departamento con ID " + idDepartamento + " no existe");
            throw new IllegalArgumentException("El departamento con ID " + idDepartamento + " no existe");
        });
        logger.info("Departamento con ID " + idDepartamento + " encontrado exitosamente");
        return departamento;
    }

    @Transactional
    public List<Departamento> findAllDepartamentos() {
        logger.info("Obteniendo todos los departamentos");
        List<Departamento> departamentos = this.departamentoRepository.findAll();
        logger.info("Total de departamentos encontrados: " + departamentos.size());
        return departamentos;
    }

    @Transactional
    public Departamento updateDepartamento(Long idDepartamento, Departamento unDepartamento) {
        logger.info("Actualizando el departamento con ID: " + idDepartamento);

        Departamento departamentoExistente = this.departamentoRepository.findById(idDepartamento).orElseThrow(() -> {
            logger.warn("El departamento con ID " + idDepartamento + " no existe");
            throw new IllegalArgumentException("El departamento con ID " + idDepartamento + " no existe");
        });

        Empresa empresa = empresaRepository.findById(unDepartamento.getEmpresa().getId()).orElseThrow(() -> {
            logger.warn("La empresa con ID " + unDepartamento.getEmpresa().getId() + " no existe");
            throw new IllegalArgumentException("La empresa con ID " + unDepartamento.getEmpresa().getId() + " no existe");
        });

        departamentoExistente.setNombre(unDepartamento.getNombre());
        departamentoExistente.setEmpresa(empresa);

        Departamento updated = this.departamentoRepository.save(departamentoExistente);
        logger.info("Departamento con ID " + idDepartamento + " actualizado exitosamente");
        return updated;
    }
}
