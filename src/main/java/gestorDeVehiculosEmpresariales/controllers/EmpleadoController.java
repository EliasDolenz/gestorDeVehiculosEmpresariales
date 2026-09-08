package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.empleado.EmpleadoUpdateDTO;
import gestorDeVehiculosEmpresariales.services.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {
    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping("/{idEmpleado}")
    public ResponseEntity<EmpleadoResponseDTO> getEmpleadoByID(@PathVariable Long idEmpleado) {
        EmpleadoResponseDTO empleado = this.empleadoService.findEmpleadoById(idEmpleado);
        return ResponseEntity.ok(empleado);
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoSimpleDTO>> getAllEmpleados() {
        List<EmpleadoSimpleDTO> empleados = this.empleadoService.findAllEmpleado();
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/por-empresa/{idEmpresa}")
    public ResponseEntity<List<EmpleadoSimpleDTO>> getEmpleadosByEmpresaId(@PathVariable Long idEmpresa) {
        List<EmpleadoSimpleDTO> empleados = this.empleadoService.findEmpleadosByIdEmpresa(idEmpresa);
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/por-departamento/{idDepartamento}")
    public ResponseEntity<List<EmpleadoSimpleDTO>> getEmpleadosByDepartamentoId(@PathVariable Long idDepartamento) {
        List<EmpleadoSimpleDTO> empleados = this.empleadoService.findEmpleadosByIdDepartamento(idDepartamento);
        return ResponseEntity.ok(empleados);
    }

    @PutMapping("/{idEmpleado}")
    public ResponseEntity<EmpleadoResponseDTO> updateEmpleado(@PathVariable Long idEmpleado, @Valid @RequestBody EmpleadoUpdateDTO empleadoUpdateDTO) {
        EmpleadoResponseDTO empleadoActualizado = this.empleadoService.updateEmpleado(idEmpleado, empleadoUpdateDTO);
        return ResponseEntity.ok(empleadoActualizado);
    }

    @PostMapping()
    public ResponseEntity<EmpleadoResponseDTO> createEmpleado(@Valid @RequestBody EmpleadoCreateDTO empleadoCreateDTO) {
        EmpleadoResponseDTO empleadoNuevo = this.empleadoService.saveEmpleado(empleadoCreateDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(empleadoNuevo.id()).toUri();

        return ResponseEntity.created(location).body(empleadoNuevo);
    }

    @DeleteMapping("/{idEmpleado}")
    public ResponseEntity<Void> deleteEmpleado(@PathVariable Long idEmpleado) {
        this.empleadoService.deleteEmpleadoById(idEmpleado);
        return ResponseEntity.noContent().build();
    }

}
