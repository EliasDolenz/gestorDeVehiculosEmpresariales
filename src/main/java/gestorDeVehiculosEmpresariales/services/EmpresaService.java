package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.Empresa;
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
    public Empresa saveEmpresa(Empresa unaEmpresa) {
        logger.info("Guardando nueva empresa con dirección: " + unaEmpresa.getDireccion());
        if (empresaRepository.existsByDireccion(unaEmpresa.getDireccion())) {
            logger.warn("Ya existe una empresa con la dirección: " + unaEmpresa.getDireccion());
            throw new IllegalStateException("Ya existe una empresa con esa dirección.");
        }
        logger.info("Empresa guardada exitosamente con dirección: " + unaEmpresa.getDireccion());
        return empresaRepository.save(unaEmpresa);
    }

    @Transactional
    public Empresa findEmpresaById(Long idEmpresa) {
        logger.info("Buscando empresa con ID: " + idEmpresa);
        return empresaRepository.findById(idEmpresa).orElseThrow(() -> new IllegalStateException("No se encontró la empresa con el ID: " + idEmpresa));
    }

    @Transactional
    public List<Empresa> findAllEmpresa() {
        logger.info("Buscando todas las empresas");
        return empresaRepository.findAll();
    }

    @Transactional
    public Empresa updateEmpresa(Long idEmpresa, Empresa unaEmpresa) {
        logger.info("Actualizando empresa con ID: " + idEmpresa);
        Empresa empresaExistente = this.findEmpresaById(idEmpresa);
        if (!empresaExistente.getDireccion().equals(unaEmpresa.getDireccion())) {
            logger.info("La empresa no puede modificar su dirección. Dirección actual: " + empresaExistente.getDireccion() + ", Dirección solicitada: " + unaEmpresa.getDireccion());
            throw new IllegalStateException("la empresa no puede modificar su dirección");
        }
        empresaExistente.setNombre(unaEmpresa.getNombre());
        logger.info("Empresa con ID: " + idEmpresa + " actualizada exitosamente. Nuevo nombre: " + unaEmpresa.getNombre());
        return empresaRepository.save(empresaExistente);
    }

    @Transactional
    public Boolean deleteEmpresa(Long idEmpresa) {
        logger.info("Eliminando empresa con ID: " + idEmpresa);
        Empresa empresaExistente = this.findEmpresaById(idEmpresa);
        if (empresaExistente.getDepartamentos().isEmpty()) {
            empresaRepository.delete(empresaExistente);
            logger.info("Empresa con ID: " + idEmpresa + " eliminada exitosamente.");
            return Boolean.TRUE;
        } else {
            logger.warn("No se puede eliminar la empresa con ID: " + idEmpresa + " porque tiene departamentos asociados.");
            throw new IllegalStateException("No se puede eliminar la empresa porque tiene departamentos asociados.");
        }
    }
}
