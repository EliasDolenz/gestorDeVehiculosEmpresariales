package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.config.RespuestasDeError;
import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoUpdateDTO;
import gestorDeVehiculosEmpresariales.services.EmpleadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Empleados", description = "Endpoints para gestionar empleados")
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {
    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @Operation(summary = "Obtener empleado por ID", description = "Obtiene un empleado específico por su ID")@RespuestasDeError
    @GetMapping("/{idEmpleado}")
    public ResponseEntity<EmpleadoResponseDTO> getEmpleadoByID(@PathVariable Long idEmpleado) {
        EmpleadoResponseDTO empleado = this.empleadoService.findEmpleadoById(idEmpleado);
        return ResponseEntity.ok(empleado);
    }

    @Operation(summary = "Obtener todos los empleados", description = "Obtiene una lista de todos los empleados registrados")@RespuestasDeError
    @GetMapping
    public ResponseEntity<List<EmpleadoSimpleDTO>> getAllEmpleados() {
        List<EmpleadoSimpleDTO> empleados = this.empleadoService.findAllEmpleado();
        return ResponseEntity.ok(empleados);
    }

    @Operation(summary = "Obtener empleados por empresa", description = "Obtiene una lista de empleados asociados a una empresa específica por su ID")@RespuestasDeError
    @GetMapping("/por-empresa/{idEmpresa}")
    public ResponseEntity<List<EmpleadoSimpleDTO>> getEmpleadosByEmpresaId(@PathVariable Long idEmpresa) {
        List<EmpleadoSimpleDTO> empleados = this.empleadoService.findEmpleadosByIdEmpresa(idEmpresa);
        return ResponseEntity.ok(empleados);
    }

    @Operation(summary = "Obtener empleados por departamento", description = "Obtiene una lista de empleados asociados a un departamento específico por su ID")@RespuestasDeError
    @GetMapping("/por-departamento/{idDepartamento}")
    public ResponseEntity<List<EmpleadoSimpleDTO>> getEmpleadosByDepartamentoId(@PathVariable Long idDepartamento) {
        List<EmpleadoSimpleDTO> empleados = this.empleadoService.findEmpleadosByIdDepartamento(idDepartamento);
        return ResponseEntity.ok(empleados);
    }

    @Operation(summary = "Actualizar empleado", description = "Actualiza la información de un empleado existente por su ID")@RespuestasDeError
    @PutMapping("/{idEmpleado}")
    public ResponseEntity<EmpleadoResponseDTO> updateEmpleado(@PathVariable Long idEmpleado, @Valid @RequestBody EmpleadoUpdateDTO empleadoUpdateDTO) {
        EmpleadoResponseDTO empleadoActualizado = this.empleadoService.updateEmpleado(idEmpleado, empleadoUpdateDTO);
        return ResponseEntity.ok(empleadoActualizado);
    }

    @Operation(summary = "Crear empleado", description = "Crea un nuevo empleado en el sistema")@RespuestasDeError
    @PostMapping()
    public ResponseEntity<EmpleadoResponseDTO> createEmpleado(@Valid @RequestBody EmpleadoCreateDTO empleadoCreateDTO) {
        EmpleadoResponseDTO empleadoNuevo = this.empleadoService.saveEmpleado(empleadoCreateDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(empleadoNuevo.id()).toUri();

        return ResponseEntity.created(location).body(empleadoNuevo);
    }

    @Operation(summary = "Eliminar empleado", description = "Elimina un empleado específico por su ID")@RespuestasDeError
    @DeleteMapping("/{idEmpleado}")
    public ResponseEntity<Void> deleteEmpleado(@PathVariable Long idEmpleado) {
        this.empleadoService.deleteEmpleadoById(idEmpleado);
        return ResponseEntity.noContent().build();
    }

}
