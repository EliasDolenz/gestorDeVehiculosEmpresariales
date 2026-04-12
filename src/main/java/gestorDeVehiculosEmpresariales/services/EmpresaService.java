package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.Empresa;
import gestorDeVehiculosEmpresariales.repositories.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {
    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public Empresa saveEmpresa(Empresa unaEmpresa) {
        if (empresaRepository.existsByDireccion(unaEmpresa.getDireccion())) {
            throw new IllegalStateException("Ya existe una empresa con esa dirección.");
        }
        return empresaRepository.save(unaEmpresa);
    }

    public Empresa findEmpresaById(Long idEmpresa) {
        return empresaRepository.findById(idEmpresa).orElseThrow(() -> new IllegalStateException("No se encontró la empresa con el ID: " + idEmpresa));
    }

    public List<Empresa> findAllEmpresa() {
        return empresaRepository.findAll();
    }

    public Empresa updateEmpresa(Long idEmpresa, Empresa unaEmpresa) {
        Empresa empresaExistente = this.findEmpresaById(idEmpresa);
        if (!empresaExistente.getDireccion().equals(unaEmpresa.getDireccion())) {
            throw new IllegalStateException("la empresa no puede modificar su dirección");
        }
        empresaExistente.setNombre(unaEmpresa.getNombre());
        return empresaRepository.save(empresaExistente);
    }


    public Boolean deleteEmpresa(Long idEmpresa) {
        Empresa empresaExistente = this.findEmpresaById(idEmpresa);
        if (empresaExistente.getDepartamentos().isEmpty()) {
            empresaRepository.delete(empresaExistente);
            return Boolean.TRUE;
        } else {
            throw new IllegalStateException("No se puede eliminar la empresa porque tiene departamentos asociados.");
        }
    }
}
