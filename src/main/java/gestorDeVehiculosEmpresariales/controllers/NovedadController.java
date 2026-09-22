package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.config.RespuestasDeError;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadCreateDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadResponseDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadUpdateDTO;
import gestorDeVehiculosEmpresariales.services.NovedadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Novedades", description = "Endpoints para gestionar novedades")
@RestController
@RequestMapping("/api/novedades")
public class NovedadController {
    private final NovedadService novedadService;

    public NovedadController(NovedadService novedadService) {
        this.novedadService = novedadService;
    }

    @Operation(summary = "Obtener una novedad por su ID", description = "Devuelve los detalles de una novedad específica según su ID.")@RespuestasDeError
    @GetMapping("/{idNovedad}")
    public ResponseEntity<NovedadResponseDTO> getNovedadById(@PathVariable Long idNovedad) {
        NovedadResponseDTO novedad = this.novedadService.getNovedadById(idNovedad);
        return ResponseEntity.ok(novedad);
    }
    @Operation(summary = "Obtener todas las novedades", description = "Devuelve una lista de todas las novedades registradas en el sistema.")@RespuestasDeError
    @GetMapping
    public ResponseEntity<List<NovedadSimpleDTO>> getAllNovedades() {
        List<NovedadSimpleDTO> novedades = this.novedadService.getNovedades();
        return ResponseEntity.ok(novedades);
    }
    @Operation(summary = "Crear una nueva novedad", description = "Permite crear una nueva novedad en el sistema.")@RespuestasDeError
    @PostMapping
    public ResponseEntity<NovedadResponseDTO> createNovedad(@Valid @RequestBody NovedadCreateDTO unaNovedad) {
        NovedadResponseDTO novedadNueva = this.novedadService.saveNovedad(unaNovedad);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(novedadNueva.id()).toUri();

        return ResponseEntity.created(location).body(novedadNueva);
    }

    @Operation(summary = "Actualizar una novedad existente", description = "Permite actualizar los detalles de una novedad específica.")@RespuestasDeError
    @PutMapping("/{idNovedad}")
    public ResponseEntity<NovedadResponseDTO> updateNovedad(@PathVariable Long idNovedad, @Valid @RequestBody NovedadUpdateDTO unaNovedad) {
        NovedadResponseDTO novedadActualizada = this.novedadService.updateNovedad(idNovedad, unaNovedad);
        return ResponseEntity.ok(novedadActualizada);
    }

    @Operation(summary = "Eliminar una novedad", description = "Permite eliminar una novedad específica según su ID.")@RespuestasDeError
    @DeleteMapping("/{idNovedad}")
    public ResponseEntity<Void> deleteNovedad(@PathVariable Long idNovedad) {
        this.novedadService.deleteNovedad(idNovedad);
        return ResponseEntity.noContent().build();
    }

}
