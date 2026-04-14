package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.entities.Empleado;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpleadoService {
    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public Empleado saveEmpleado(Empleado unEmpleado) {
        logger.info("Guardando nuevo empleado: " + unEmpleado.getNombre() + " " + unEmpleado.getApellido());
        if (empleadoRepository.existsByCorreoElectronico(unEmpleado.getCorreoElectronico())) {
            logger.warn("El correo electrónico " + unEmpleado.getCorreoElectronico() + " ya está registrado.");
            throw new IllegalArgumentException("El correo electrónico " + unEmpleado.getCorreoElectronico() + " ya está registrado.");
        }
        if (unEmpleado.getNumeroTelefono().length() > 10) {
            logger.warn("El número de teléfono " + unEmpleado.getNumeroTelefono() + " no puede tener más de 10 dígitos.");
            throw new IllegalArgumentException("El número de teléfono no puede tener más de 10 dígitos.");
        }

        if ((unEmpleado.getPinCarga().length() != 4)) {
            logger.warn("El pin de carga " + unEmpleado.getPinCarga() + " debe tener exactamente 4 dígitos.");
            throw new IllegalArgumentException("El pin de carga debe tener exactamente 4 dígitos.");
        }

        logger.info("Empleado " + unEmpleado.getNombre() + " " + unEmpleado.getApellido() + " guardado exitosamente.");
        return empleadoRepository.save(unEmpleado);
    }

    @Transactional
    public Empleado updateEmpleado(Long idEmpleado, Empleado unEmpleado) {
        logger.info("Actualizando empleado con id: " + idEmpleado);
        Empleado empleadoExistente = empleadoRepository.findById(idEmpleado).orElseThrow(() -> {
            logger.warn("No se encontró el empleado con id: " + idEmpleado);
            return
                    new IllegalArgumentException("El empleado con id " + idEmpleado + " no existe.");
        });

        if (!empleadoExistente.getNombre().equals(unEmpleado.getNombre())) {
            logger.warn("No se puede modificar el nombre del empleado con id: " + idEmpleado);
            throw new IllegalArgumentException("El Nombre del empleado no puede ser modificado.");
        }

        if (!empleadoExistente.getApellido().equals(unEmpleado.getApellido())) {
            logger.warn("No se puede modificar el apellido del empleado con id: " + idEmpleado);
            throw new IllegalArgumentException("Apellido del empleado no puede ser modificado.");
        }

        if (!(empleadoExistente.getCorreoElectronico().equals(unEmpleado.getCorreoElectronico()))) {
            if (empleadoRepository.existsByCorreoElectronico(unEmpleado.getCorreoElectronico())) {
                logger.warn("El correo electrónico " + unEmpleado.getCorreoElectronico() + " ya está registrado por otro empleado.");
                throw new IllegalArgumentException("El correo electrónico esta siendo utilizado por otro empleado.");
            }
        }

        if (!(empleadoExistente.getNumeroTelefono().equals(unEmpleado.getNumeroTelefono()))) {
            if (empleadoRepository.existsByNumeroTelefono(unEmpleado.getNumeroTelefono())) {
                logger.warn("El número de teléfono " + unEmpleado.getNumeroTelefono() + " ya está registrado por otro empleado.");
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
        Empleado saved = empleadoRepository.save(empleadoExistente);
        logger.info("Empleado con id: " + idEmpleado + " actualizado exitosamente.");
        return (saved);
    }

    @Transactional
    public Empleado findEmpleadoById(Long idEmpleado) {
        logger.info("Buscando empleado con id: " + idEmpleado);

        return empleadoRepository.findById(idEmpleado).orElseThrow(() -> new IllegalArgumentException("El empleado con id " + idEmpleado + " no existe."));
    }

    @Transactional
    public List<Empleado> findAllEmpleado() {
        logger.info("Obteniendo lista de todos los empleados");
        return empleadoRepository.findAll();
    }

    @Transactional
    public Boolean deleteEmpleadoById(Long idEmpleado) {
        logger.info("Eliminando empleado con id: " + idEmpleado);
        if (empleadoRepository.existsById(idEmpleado)) {
            empleadoRepository.deleteById(idEmpleado);
            logger.info("Empleado con id: " + idEmpleado + " eliminado exitosamente.");
            return Boolean.TRUE;
        } else {
            logger.warn("No se encontró el empleado con id: " + idEmpleado);
            throw new IllegalArgumentException("El empleado con id " + idEmpleado + " no existe.");

        }
    }
}
