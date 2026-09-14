package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaCreateDTO;
import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaResponseDTO;
import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaUpdateDTO;
import gestorDeVehiculosEmpresariales.entities.Empresa;
import gestorDeVehiculosEmpresariales.exceptions.RecursoNoEncontradoException;
import gestorDeVehiculosEmpresariales.exceptions.ReglaDeNegocioException;
import gestorDeVehiculosEmpresariales.mappers.EmpresaMapper;
import gestorDeVehiculosEmpresariales.repositories.EmpresaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaService {
    private static final Logger logger = LoggerFactory.getLogger(EmpresaService.class);
    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional
    public EmpresaResponseDTO saveEmpresa(EmpresaCreateDTO unaEmpresa) {
        logger.info("Guardando nueva empresa con dirección: " + unaEmpresa.direccion());
        if (empresaRepository.existsByDireccion(unaEmpresa.direccion())) {
            logger.warn("Ya existe una empresa con la dirección: " + unaEmpresa.direccion());
            throw new ReglaDeNegocioException("Ya existe una empresa con esa dirección.");
        }

        Empresa empresa = new Empresa();
        empresa.setNombre(unaEmpresa.nombre());
        empresa.setDireccion(unaEmpresa.direccion());

        Empresa empresaGuardada = empresaRepository.save(empresa);
        EmpresaResponseDTO empresaDTO = EmpresaMapper.toResponseDTO(empresaGuardada);

        logger.info("Empresa guardada exitosamente con dirección: " + unaEmpresa.direccion());
        return empresaDTO;
    }

    @Transactional(readOnly = true)
    public EmpresaResponseDTO findEmpresaById(Long idEmpresa) {
        logger.info("Buscando empresa con ID: " + idEmpresa);
        Empresa empresa = this.obtenerEmpresaPorId(idEmpresa);

        logger.info("Empresa encontrada con ID: " + idEmpresa + ", nombre: " + empresa.getNombre() + ", dirección: " + empresa.getDireccion());
        return EmpresaMapper.toResponseDTO(empresa);
    }

    @Transactional(readOnly = true)
    public List<EmpresaSimpleDTO> findAllEmpresa() {
        logger.info("Buscando todas las empresas");

        List<Empresa> empresas = empresaRepository.findAll();

        List<EmpresaSimpleDTO> empresasDTOs = empresas.stream().map(EmpresaMapper::toSimpleDTO).toList();

        logger.info("Se encontraron " + empresasDTOs.size() + " empresas");

        return empresasDTOs;
    }

    @Transactional
    public EmpresaResponseDTO updateEmpresa(Long idEmpresa, EmpresaUpdateDTO unaEmpresaDTO) {
        logger.info("Actualizando empresa con ID: " + idEmpresa);
        Empresa empresaExistente = this.obtenerEmpresaPorId(idEmpresa);

        empresaExistente.setNombre(unaEmpresaDTO.nombre());
        logger.info("Empresa con ID: " + idEmpresa + " actualizada exitosamente. Nuevo nombre: " + unaEmpresaDTO.nombre());
        return EmpresaMapper.toResponseDTO(empresaExistente);
    }

    @Transactional
    public Boolean deleteEmpresa(Long idEmpresa) {
        logger.info("Eliminando empresa con ID: " + idEmpresa);
        Empresa empresaExistente = this.obtenerEmpresaPorId(idEmpresa);
        if (empresaExistente.getDepartamentos().isEmpty()) {
            empresaRepository.delete(empresaExistente);
            logger.info("Empresa con ID: " + idEmpresa + " eliminada exitosamente.");
            return Boolean.TRUE;
        } else {
            logger.warn("No se puede eliminar la empresa con ID: " + idEmpresa + " porque tiene departamentos asociados.");
            throw new ReglaDeNegocioException("No se puede eliminar la empresa porque tiene departamentos asociados.");
        }
    }

    private Empresa obtenerEmpresaPorId(Long idEmpresa) {
        return empresaRepository.findById(idEmpresa).orElseThrow(() -> {
            logger.warn("No se encontró la empresa con el ID: " + idEmpresa);
            return new RecursoNoEncontradoException("No se encontró la empresa con el ID: " + idEmpresa);
        });
    }
}