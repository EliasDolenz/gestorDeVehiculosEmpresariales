package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.config.RespuestasDeError;
import gestorDeVehiculosEmpresariales.dto.uso.TerminarUsoDTO;
import gestorDeVehiculosEmpresariales.dto.uso.UsoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.uso.UsoResponseDTO;
import gestorDeVehiculosEmpresariales.services.UsoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Usos", description = "Endpoints para gestionar los usos de los vehículos")
@RestController
@RequestMapping("/api/usos")
public class UsoController {
    private final UsoService usoService;

    public UsoController(UsoService usoService) {
        this.usoService = usoService;
    }

    @Operation(summary = "Obtener un uso por su ID")@RespuestasDeError
    @GetMapping("/{idUso}")
    public ResponseEntity<UsoResponseDTO> getUsoByID(@PathVariable Long idUso) {
        UsoResponseDTO uso = this.usoService.findUsoById(idUso);
        return ResponseEntity.ok(uso);
    }

    @Operation(summary = "Obtener todos los usos")@RespuestasDeError
    @GetMapping
    public ResponseEntity<List<UsoResponseDTO>> getAllUsos() {
        List<UsoResponseDTO> usos = this.usoService.findAllUso();

        return ResponseEntity.ok(usos);
    }

    @Operation(summary = "Comenzar un nuevo uso")@RespuestasDeError
    @PostMapping
    public ResponseEntity<UsoResponseDTO> comenzarUso(@Valid @RequestBody UsoCreateDTO unUso) {
        UsoResponseDTO usoNuevo = this.usoService.comenzarUso(unUso);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(usoNuevo.id()).toUri();

        return ResponseEntity.created(location).body(usoNuevo);
    }

    @Operation(summary = "Finalizar un uso")@RespuestasDeError
    @PutMapping("/{idUso}/finalizar")
    public ResponseEntity<UsoResponseDTO> terminarUso(@PathVariable Long idUso, @Valid @RequestBody TerminarUsoDTO datosFinalizacion) {
        UsoResponseDTO usoFinalizado = this.usoService.terminarUso(idUso, datosFinalizacion);
        return ResponseEntity.ok(usoFinalizado);
    }

    @Operation(summary = "Eliminar un uso")@RespuestasDeError
    @DeleteMapping("/{idUso}")
    public ResponseEntity<Void> eliminarUso(@PathVariable Long idUso) {
        this.usoService.deleteUso(idUso);
        return ResponseEntity.noContent().build();
    }
}
