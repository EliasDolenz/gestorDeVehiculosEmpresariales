package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.Departamento;
import gestorDeVehiculosEmpresariales.entities.Empresa;
import gestorDeVehiculosEmpresariales.repositories.DepartamentoRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpresaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DepartamentoService {
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
        Empresa empresa = empresaRepository.findById(unDepartamento.getEmpresa().getId()).orElseThrow(() -> new IllegalArgumentException("La empresa con ID " + unDepartamento.getEmpresa().getId() + " no existe"));

        unDepartamento.setEmpresa(empresa);
        return departamentoRepository.save(unDepartamento);
    }

    @Transactional
    public Boolean deleteDepartamento(Long idDepartamento) {
        if (!departamentoRepository.existsById(idDepartamento)) {
            throw new IllegalArgumentException("El departamento con ID " + idDepartamento + " no existe");
        }

        if (empleadoRepository.countByDepartamentoId(idDepartamento) > 0) {
            throw new IllegalArgumentException("No se puede eliminar el Departamento porque hay empleados en el mismo");
        }

        departamentoRepository.deleteById(idDepartamento);
        return Boolean.TRUE;

    }
}
