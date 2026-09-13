package gestorDeVehiculosEmpresariales.services;

import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoUpdateDTO;
import gestorDeVehiculosEmpresariales.entities.Empleado;
import gestorDeVehiculosEmpresariales.exceptions.RecursoNoEncontradoException;
import gestorDeVehiculosEmpresariales.exceptions.ReglaDeNegocioException;
import gestorDeVehiculosEmpresariales.mappers.EmpleadoMapper;
import gestorDeVehiculosEmpresariales.repositories.DepartamentoRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpleadoRepository;
import gestorDeVehiculosEmpresariales.repositories.EmpresaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class EmpleadoService {
    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);
    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final EmpresaRepository empresaRepository;


    public EmpleadoService(EmpleadoRepository empleadoRepository, DepartamentoRepository departamentoRepository, EmpresaRepository empresaRepository) {
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
        this.empresaRepository = empresaRepository;
    }

    @Transactional
    public EmpleadoResponseDTO saveEmpleado(EmpleadoCreateDTO unEmpleado) {
        logger.info("Guardando nuevo empleado: " + unEmpleado.nombre() + " " + unEmpleado.apellido());
        if (empleadoRepository.existsByCorreoElectronico(unEmpleado.correoElectronico())) {
            logger.warn("El correo electrónico " + unEmpleado.correoElectronico() + " ya está registrado.");
            throw new ReglaDeNegocioException("El correo electrónico " + unEmpleado.correoElectronico() + " ya está registrado.");
        }

        Empleado empleado = new Empleado();
        empleado.setNombre(unEmpleado.nombre());
        empleado.setApellido(unEmpleado.apellido());
        empleado.setNumeroTelefono(unEmpleado.numeroTelefono());
        empleado.setDepartamento(departamentoRepository.findById(unEmpleado.departamentoId()).orElseThrow(() -> {
            logger.warn("El departamento con id " + unEmpleado.departamentoId() + " no existe. No se puede asignar al empleado " + unEmpleado.nombre() + " " + unEmpleado.apellido());
            return new RecursoNoEncontradoException("El departamento con id " + unEmpleado.departamentoId() + " no existe. No se puede asignar al empleado.");
        }));
        empleado.setCorreoElectronico(unEmpleado.correoElectronico());
        empleado.setPuesto(unEmpleado.puesto());
        empleado.setTieneRegistroConducir(unEmpleado.tieneRegistroConducir());
        if (unEmpleado.tieneRegistroConducir()) {
            empleado.setVencimientoLicencia(unEmpleado.vencimientoLicencia());
        } else {
            empleado.setVencimientoLicencia(null);
        }
        empleado.setPinCarga(unEmpleado.pinCarga());
        empleado.setEmpresa(empresaRepository.findById(unEmpleado.empresaId()).orElseThrow(() -> {
            logger.warn("La empresa con id " + unEmpleado.empresaId() + " no existe. No se puede asignar al empleado " + unEmpleado.nombre() + " " + unEmpleado.apellido());
            return new RecursoNoEncontradoException("La empresa con id " + unEmpleado.empresaId() + " no existe. No se puede asignar al empleado.");
        }));

        Empleado saved = empleadoRepository.save(empleado);

        logger.info("Empleado " + unEmpleado.nombre() + " " + unEmpleado.apellido() + " guardado exitosamente.");
        return EmpleadoMapper.toResponseDTO(saved);
    }

    @Transactional
    public EmpleadoResponseDTO updateEmpleado(Long idEmpleado, EmpleadoUpdateDTO unEmpleado) {
        logger.info("Actualizando empleado con id: " + idEmpleado);
        Empleado empleadoExistente = empleadoRepository.findById(idEmpleado).orElseThrow(() -> {
            logger.warn("No se encontró el empleado con id: " + idEmpleado);
            return
                    new RecursoNoEncontradoException("El empleado con id " + idEmpleado + " no existe.");
        });

        if (!(empleadoExistente.getCorreoElectronico().equals(unEmpleado.correoElectronico()))) {
            if (empleadoRepository.existsByCorreoElectronico(unEmpleado.correoElectronico())) {
                logger.warn("El correo electrónico " + unEmpleado.correoElectronico() + " ya está registrado por otro empleado.");
                throw new ReglaDeNegocioException("El correo electrónico esta siendo utilizado por otro empleado.");
            }
        }

        if (!(empleadoExistente.getNumeroTelefono().equals(unEmpleado.numeroTelefono()))) {
            if (empleadoRepository.existsByNumeroTelefono(unEmpleado.numeroTelefono())) {
                logger.warn("El número de teléfono " + unEmpleado.numeroTelefono() + " ya está registrado por otro empleado.");
                throw new ReglaDeNegocioException("El número de teléfono esta siendo utilizado por otro empleado.");
            }
        }

        empleadoExistente.setDepartamento(departamentoRepository.findById(unEmpleado.departamentoId()).orElseThrow(() -> new RecursoNoEncontradoException("El departamento con id " + unEmpleado.departamentoId() + " no existe.")));
        empleadoExistente.setPuesto(unEmpleado.puesto());
        empleadoExistente.setTieneRegistroConducir(unEmpleado.tieneRegistroConducir());
        if (unEmpleado.tieneRegistroConducir()) {
            empleadoExistente.setVencimientoLicencia(unEmpleado.vencimientoLicencia());
        } else {
            empleadoExistente.setVencimientoLicencia(null);
        }
        empleadoExistente.setPinCarga(unEmpleado.pinCarga());
        
        empleadoExistente.setCorreoElectronico(unEmpleado.correoElectronico());
        empleadoExistente.setNumeroTelefono(unEmpleado.numeroTelefono());
        Empleado saved = empleadoRepository.save(empleadoExistente);
        logger.info("Empleado con id: " + idEmpleado + " actualizado exitosamente.");
        return EmpleadoMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public EmpleadoResponseDTO findEmpleadoById(Long idEmpleado) {
        logger.info("Buscando empleado con id: " + idEmpleado);
        Empleado empleadoExistente = empleadoRepository.findById(idEmpleado).orElseThrow(() -> {
            logger.warn("No se encontró el empleado con id: " + idEmpleado);
            return new RecursoNoEncontradoException("El empleado con id " + idEmpleado + " no existe.");
        });
        logger.info("Empleado con id: " + idEmpleado + " encontrado exitosamente.");
        return EmpleadoMapper.toResponseDTO(empleadoExistente);
    }


    @Transactional(readOnly = true)
    public List<EmpleadoSimpleDTO> findAllEmpleado() {
        logger.info("Obteniendo lista de todos los empleados");
        List<Empleado> empleados = empleadoRepository.findAll();

        List<EmpleadoSimpleDTO> empleadosDto = empleados.stream()
                .map(EmpleadoMapper::toSimpleDTO)
                .toList();
        logger.info("Se encontraron " + empleadosDto.size() + " empleados en total.");
        return empleadosDto;
    }

    @Transactional(readOnly = true)
    public List<EmpleadoSimpleDTO> findEmpleadosByIdEmpresa(Long idEmpresa) {
        logger.info("Obteniendo lista de empleados para la empresa con id: " + idEmpresa);
        List<Empleado> empleados = empleadoRepository.findByEmpresaId(idEmpresa);

        List<EmpleadoSimpleDTO> empleadosDto = empleados.stream()
                .map(EmpleadoMapper::toSimpleDTO)
                .toList();
        logger.info("Se encontraron " + empleadosDto.size() + " empleados en total.");
        return empleadosDto;
    }

    @Transactional(readOnly = true)
    public List<EmpleadoSimpleDTO> findEmpleadosByIdDepartamento(Long idDepartamento) {
        logger.info("Obteniendo lista de empleados para el departamento con id: " + idDepartamento);
        List<Empleado> empleados = empleadoRepository.findByDepartamentoId(idDepartamento);

        List<EmpleadoSimpleDTO> empleadosDto = empleados.stream()
                .map(EmpleadoMapper::toSimpleDTO)
                .toList();
        logger.info("Se encontraron " + empleadosDto.size() + " empleados en total.");
        return empleadosDto;
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
            throw new RecursoNoEncontradoException("El empleado con id " + idEmpleado + " no existe.");
        }
    }
}
