package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.entities.Departamento;
import gestorDeVehiculosEmpresariales.services.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/departamento")
public class DepartamentoController {
    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping("/{idDepartamento}")
    public ResponseEntity<Departamento> getDepartamentoByID(@PathVariable Long idDepartamento) {
        Departamento departamento = this.departamentoService.findDepartamentoById(idDepartamento);
        return ResponseEntity.ok(departamento);
    }

    @GetMapping
    public ResponseEntity<List<Departamento>> getAllDepartamentos() {
        List<Departamento> departamentos = this.departamentoService.findAllDepartamentos();
        return ResponseEntity.ok(departamentos);
    }

    @PutMapping("/{idDepartamento}")
    public ResponseEntity<Departamento> updateDepartamento(@PathVariable Long idDepartamento, @Valid @RequestBody Departamento unDepartamento) {
        Departamento departamentoActualizado = this.departamentoService.updateDepartamento(idDepartamento, unDepartamento);
        return ResponseEntity.ok(departamentoActualizado);
    }

    @PostMapping()
    public ResponseEntity<Departamento> createDepartamento(@Valid @RequestBody Departamento unDepartamento) {
        Departamento departamentoNuevo = this.departamentoService.saveDepartamento(unDepartamento);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(departamentoNuevo.getId()).toUri();

        return ResponseEntity.created(location).body(departamentoNuevo);
    }

    @DeleteMapping("/{idDepartamento}")
    public ResponseEntity<Void> deleteDepartamento(@PathVariable Long idDepartamento) {
        this.departamentoService.deleteDepartamento(idDepartamento);
        return ResponseEntity.noContent().build();
    }


}
