package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.config.RespuestasDeError;
import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaCreateDTO;
import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaResponseDTO;
import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.empresa.EmpresaUpdateDTO;
import gestorDeVehiculosEmpresariales.services.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Empresas", description = "Operaciones relacionadas con la gestión de empresas")
@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {
    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @Operation(summary = "Obtiene una empresa por su ID", description = "Devuelve los detalles de una empresa específica según su ID.")@RespuestasDeError
    @GetMapping("/{idEmpresa}")
    public ResponseEntity<EmpresaResponseDTO> getEmpresaByID(@PathVariable Long idEmpresa) {
        EmpresaResponseDTO empresa = this.empresaService.findEmpresaById(idEmpresa);
        return ResponseEntity.ok(empresa);
    }

    @Operation(summary = "Obtiene todas las empresas", description = "Devuelve una lista de todas las empresas registradas en el sistema.")@RespuestasDeError
    @GetMapping
    public ResponseEntity<List<EmpresaSimpleDTO>> getAllEmpresas() {
        List<EmpresaSimpleDTO> empresas = this.empresaService.findAllEmpresa();
        return ResponseEntity.ok(empresas);
    }

    @Operation(summary = "Crea una nueva empresa", description = "Permite crear una nueva empresa en el sistema.")@RespuestasDeError
    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> createEmpresa(@Valid @RequestBody EmpresaCreateDTO unaEmpresa) {
        EmpresaResponseDTO empresaNueva = this.empresaService.saveEmpresa(unaEmpresa);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(empresaNueva.id()).toUri();

        return ResponseEntity.created(location).body(empresaNueva);
    }

    @Operation(summary = "Actualiza una empresa existente", description = "Permite actualizar los detalles de una empresa específica.")@RespuestasDeError
    @PutMapping("/{idEmpresa}")
    public ResponseEntity<EmpresaResponseDTO> updateEmpresa(@PathVariable Long idEmpresa, @Valid @RequestBody EmpresaUpdateDTO unaEmpresa) {
        EmpresaResponseDTO empresaActualizada = this.empresaService.updateEmpresa(idEmpresa, unaEmpresa);
        return ResponseEntity.ok(empresaActualizada);
    }

    @Operation(summary = "Elimina una empresa", description = "Permite eliminar una empresa específica según su ID.")@RespuestasDeError
    @DeleteMapping("/{idEmpresa}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable Long idEmpresa) {
        this.empresaService.deleteEmpresa(idEmpresa);
        return ResponseEntity.noContent().build();
    }
}
