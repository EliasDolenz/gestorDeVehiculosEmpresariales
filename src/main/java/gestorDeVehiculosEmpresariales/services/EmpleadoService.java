package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.Empleado;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public Empleado saveEmpleado(Empleado unEmpleado) {
        if (empleadoRepository.existsByCorreoElectronico(unEmpleado.getCorreoElectronico())) {
            throw new IllegalArgumentException("El correo electrónico " + unEmpleado.getCorreoElectronico() + " ya está registrado.");
        }
        if (unEmpleado.getNumeroTelefono().length() > 10) {
            throw new IllegalArgumentException("El número de teléfono no puede tener más de 10 dígitos.");
        }

        if ((unEmpleado.getPinCarga().length() != 4)) {
            throw new IllegalArgumentException("El pin de carga debe tener exactamente 4 dígitos.");
        }

        return empleadoRepository.save(unEmpleado);
    }

    public Empleado updateEmpleado(Long idEmpleado, Empleado unEmpleado) {
        Empleado empleadoExistente = empleadoRepository.findById(idEmpleado).orElseThrow(() -> new IllegalArgumentException("El empleado con id " + idEmpleado + " no existe."));

        if (!empleadoExistente.getNombre().equals(unEmpleado.getNombre())) {
            throw new IllegalArgumentException("El Nombre del empleado no puede ser modificado.");
        }

        if (!empleadoExistente.getApellido().equals(unEmpleado.getApellido())) {
            throw new IllegalArgumentException("Apellido del empleado no puede ser modificado.");
        }

        if (!(empleadoExistente.getCorreoElectronico().equals(unEmpleado.getCorreoElectronico()))) {
            if (empleadoRepository.existsByCorreoElectronico(unEmpleado.getCorreoElectronico())) {
                throw new IllegalArgumentException("El correo electrónico esta siendo utilizado por otro empleado.");
            }
        }

        if (!(empleadoExistente.getNumeroTelefono().equals(unEmpleado.getNumeroTelefono()))) {
            if (empleadoRepository.existsByNumeroTelefono(unEmpleado.getNumeroTelefono())) {
                throw new IllegalArgumentException("El numero de telefono esta siendo utilizado por otro empleado.");
            }
        }

        empleadoExistente.setDepartamento(unEmpleado.getDepartamento());
        empleadoExistente.setPuesto(unEmpleado.getPuesto());
        empleadoExistente.setTieneRegistroConducir(unEmpleado.getTieneRegistroConducir());
        if (unEmpleado.getTieneRegistroConducir()) {
            empleadoExistente.setVencimientoLicencia(unEmpleado.getVencimientoLicencia());
        } else {
            empleadoExistente.setVencimientoLicencia(null);
        }
        empleadoExistente.setPinCarga(unEmpleado.getPinCarga());
        empleadoExistente.setCorreoElectronico(unEmpleado.getCorreoElectronico());
        empleadoExistente.setNumeroTelefono(unEmpleado.getNumeroTelefono());

        return empleadoRepository.save(empleadoExistente);
    }

    public List<Empleado> findAllEmpleado() {
        return empleadoRepository.findAll();
    }

    public Boolean deleteEmpleadoById(Long idEmpleado) {
        if (empleadoRepository.existsById(idEmpleado)) {
            empleadoRepository.deleteById(idEmpleado);
            return Boolean.TRUE;
        } else {
            throw new IllegalArgumentException("El empleado con id " + idEmpleado + " no existe.");

        }
    }
}
