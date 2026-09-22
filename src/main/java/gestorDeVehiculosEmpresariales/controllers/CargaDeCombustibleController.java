package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.config.RespuestasDeError;
import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleCreateDTO;
import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleResponseDTO;
import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleUpdateDTO;
import gestorDeVehiculosEmpresariales.services.CargaDeCombustibleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Cargas de Combustible", description = "Endpoints para gestionar las cargas de combustible")
@RestController
@RequestMapping("/api/cargasDeCombustible")
public class CargaDeCombustibleController {
    private final CargaDeCombustibleService cargaDeCombustibleService;

    public CargaDeCombustibleController(CargaDeCombustibleService cargaDeCombustibleService) {
        this.cargaDeCombustibleService = cargaDeCombustibleService;
    }

    @Operation(summary = "Obtener carga de combustible por ID", description = "Obtiene una carga de combustible específica por su ID")@RespuestasDeError
    @GetMapping("/{idCarga}")
    public ResponseEntity<CargaDeCombustibleResponseDTO> getCargaDeCombustibleById(@PathVariable Long idCarga) {
        CargaDeCombustibleResponseDTO cargaDeCombustible = this.cargaDeCombustibleService.findCargaById(idCarga);
        return ResponseEntity.ok(cargaDeCombustible);
    }

    @Operation(summary = "Obtener todas las cargas de combustible", description = "Obtiene una lista de todas las cargas de combustible registradas")@RespuestasDeError
    @GetMapping
    public ResponseEntity<List<CargaDeCombustibleSimpleDTO>> getAllCargasDeCombustible() {
        List<CargaDeCombustibleSimpleDTO> cargasDeCombustible = this.cargaDeCombustibleService.findAllCargas();
        return ResponseEntity.ok(cargasDeCombustible);
    }

    @Operation(summary = "Actualizar carga de combustible", description = "Actualiza la información de una carga de combustible existente por su ID")@RespuestasDeError
    @PutMapping("/{idCarga}")
    public ResponseEntity<CargaDeCombustibleResponseDTO> updateCargaDeCombustible(@PathVariable Long idCarga, @Valid @RequestBody CargaDeCombustibleUpdateDTO unaCargaDeCombustible) {
        CargaDeCombustibleResponseDTO cargaDeCombustibleActualizado = this.cargaDeCombustibleService.updateCarga(idCarga, unaCargaDeCombustible);

        return ResponseEntity.ok(cargaDeCombustibleActualizado);
    }

    @Operation(summary = "Crear carga de combustible", description = "Crea una nueva carga de combustible en el sistema")@RespuestasDeError
    @PostMapping()
    public ResponseEntity<CargaDeCombustibleResponseDTO> createCargaDeCombustible(@Valid @RequestBody CargaDeCombustibleCreateDTO unaCargaDeCombustible) {
        CargaDeCombustibleResponseDTO cargaDeCombustibleNuevo = this.cargaDeCombustibleService.saveCarga(unaCargaDeCombustible);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(cargaDeCombustibleNuevo.id()).toUri();

        return ResponseEntity.created(location).body(cargaDeCombustibleNuevo);
    }

    @Operation(summary = "Eliminar carga de combustible", description = "Elimina una carga de combustible específica por su ID")@RespuestasDeError
    @DeleteMapping("/{idCarga}")
    public ResponseEntity<Void> deleteCargaDeCombustible(@PathVariable Long idCarga) {
        this.cargaDeCombustibleService.deleteCargaById(idCarga);
        return ResponseEntity.noContent().build();
    }

}
