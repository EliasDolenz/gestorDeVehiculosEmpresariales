package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoUpdateDTO;
import gestorDeVehiculosEmpresariales.services.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {
    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping("/{idDepartamento}")
    public ResponseEntity<DepartamentoResponseDTO> getDepartamentoByID(@PathVariable Long idDepartamento) {
        DepartamentoResponseDTO departamento = this.departamentoService.findDepartamentoById(idDepartamento);
        return ResponseEntity.ok(departamento);
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoSimpleDTO>> getAllDepartamentos() {
        List<DepartamentoSimpleDTO> departamentos = this.departamentoService.findAllDepartamentos();
        return ResponseEntity.ok(departamentos);
    }

    @PutMapping("/{idDepartamento}")
    public ResponseEntity<DepartamentoResponseDTO> updateDepartamento(@PathVariable Long idDepartamento, @Valid @RequestBody DepartamentoUpdateDTO unDepartamento) {
        DepartamentoResponseDTO departamentoActualizado = this.departamentoService.updateDepartamento(idDepartamento, unDepartamento);
        return ResponseEntity.ok(departamentoActualizado);
    }

    @PostMapping()
    public ResponseEntity<DepartamentoResponseDTO> createDepartamento(@Valid @RequestBody DepartamentoCreateDTO unDepartamento) {
        DepartamentoResponseDTO departamentoNuevo = this.departamentoService.saveDepartamento(unDepartamento);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(departamentoNuevo.id()).toUri();

        return ResponseEntity.created(location).body(departamentoNuevo);
    }

    @DeleteMapping("/{idDepartamento}")
    public ResponseEntity<Void> deleteDepartamento(@PathVariable Long idDepartamento) {
        this.departamentoService.deleteDepartamento(idDepartamento);
        return ResponseEntity.noContent().build();
    }


}
