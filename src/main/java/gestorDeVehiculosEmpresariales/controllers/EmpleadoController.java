package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.entities.Empleado;
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
    public ResponseEntity<Empleado> getEmpleadoByID(@PathVariable Long idEmpleado) {
        Empleado empleado = this.empleadoService.findEmpleadoById(idEmpleado);
        return ResponseEntity.ok(empleado);
    }

    @GetMapping
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        List<Empleado> Empleados = this.empleadoService.findAllEmpleado();
        return ResponseEntity.ok(Empleados);
    }

    @PutMapping("/{idEmpleado}")
    public ResponseEntity<Empleado> updateEmpleado(@PathVariable Long idEmpleado, @Valid @RequestBody Empleado unEmpleado) {
        Empleado EmpleadoActualizado = this.empleadoService.updateEmpleado(idEmpleado, unEmpleado);
        return ResponseEntity.ok(EmpleadoActualizado);
    }

    @PostMapping()
    public ResponseEntity<Empleado> createEmpleado(@Valid @RequestBody Empleado unEmpleado) {
        Empleado EmpleadoNuevo = this.empleadoService.saveEmpleado(unEmpleado);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(EmpleadoNuevo.getId()).toUri();

        return ResponseEntity.created(location).body(EmpleadoNuevo);
    }

    @DeleteMapping("/{idEmpleado}")
    public ResponseEntity<Void> deleteDepartamento(@PathVariable Long idEmpleado) {
        this.empleadoService.deleteEmpleadoById(idEmpleado);
        return ResponseEntity.noContent().build();
    }

}
