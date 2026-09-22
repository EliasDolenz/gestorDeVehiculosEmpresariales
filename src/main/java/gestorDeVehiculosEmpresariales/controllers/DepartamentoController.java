package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.config.RespuestasDeError;
import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.departamento.DepartamentoUpdateDTO;
import gestorDeVehiculosEmpresariales.services.DepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Departamentos", description = "Endpoints para la gestión de departamentos")
@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {
    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @Operation(summary = "Obtener departamento por ID", description = "Obtiene un departamento específico por su ID")@RespuestasDeError
    @GetMapping("/{idDepartamento}")
    public ResponseEntity<DepartamentoResponseDTO> getDepartamentoByID(@PathVariable Long idDepartamento) {
        DepartamentoResponseDTO departamento = this.departamentoService.findDepartamentoById(idDepartamento);
        return ResponseEntity.ok(departamento);
    }

    @Operation(summary = "Obtener todos los departamentos", description = "Obtiene una lista de todos los departamentos registrados")@RespuestasDeError
    @GetMapping
    public ResponseEntity<List<DepartamentoSimpleDTO>> getAllDepartamentos() {
        List<DepartamentoSimpleDTO> departamentos = this.departamentoService.findAllDepartamentos();
        return ResponseEntity.ok(departamentos);
    }

    @Operation(summary = "Actualizar departamento", description = "Actualiza la información de un departamento existente por su ID")@RespuestasDeError
    @PutMapping("/{idDepartamento}")
    public ResponseEntity<DepartamentoResponseDTO> updateDepartamento(@PathVariable Long idDepartamento, @Valid @RequestBody DepartamentoUpdateDTO unDepartamento) {
        DepartamentoResponseDTO departamentoActualizado = this.departamentoService.updateDepartamento(idDepartamento, unDepartamento);
        return ResponseEntity.ok(departamentoActualizado);
    }

    @Operation(summary = "Crear departamento", description = "Crea un nuevo departamento en el sistema")@RespuestasDeError
    @PostMapping()
    public ResponseEntity<DepartamentoResponseDTO> createDepartamento(@Valid @RequestBody DepartamentoCreateDTO unDepartamento) {
        DepartamentoResponseDTO departamentoNuevo = this.departamentoService.saveDepartamento(unDepartamento);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(departamentoNuevo.id()).toUri();

        return ResponseEntity.created(location).body(departamentoNuevo);
    }

    @Operation(summary = "Eliminar departamento", description = "Elimina un departamento específico por su ID")@RespuestasDeError
    @DeleteMapping("/{idDepartamento}")
    public ResponseEntity<Void> deleteDepartamento(@PathVariable Long idDepartamento) {
        this.departamentoService.deleteDepartamento(idDepartamento);
        return ResponseEntity.noContent().build();
    }


}
