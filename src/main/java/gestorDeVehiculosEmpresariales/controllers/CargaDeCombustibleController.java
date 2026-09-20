package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleCreateDTO;
import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleResponseDTO;
import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.cargaDeCombustible.CargaDeCombustibleUpdateDTO;
import gestorDeVehiculosEmpresariales.services.CargaDeCombustibleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cargasDeCombustible")
public class CargaDeCombustibleController {
    private final CargaDeCombustibleService cargaDeCombustibleService;

    public CargaDeCombustibleController(CargaDeCombustibleService cargaDeCombustibleService) {
        this.cargaDeCombustibleService = cargaDeCombustibleService;
    }

    @GetMapping("/{idCarga}")
    public ResponseEntity<CargaDeCombustibleResponseDTO> getCargaDeCombustibleById(@PathVariable Long idCarga) {
        CargaDeCombustibleResponseDTO cargaDeCombustible = this.cargaDeCombustibleService.findCargaById(idCarga);
        return ResponseEntity.ok(cargaDeCombustible);
    }

    @GetMapping
    public ResponseEntity<List<CargaDeCombustibleSimpleDTO>> getAllCargasDeCombustible() {
        List<CargaDeCombustibleSimpleDTO> cargasDeCombustible = this.cargaDeCombustibleService.findAllCargas();
        return ResponseEntity.ok(cargasDeCombustible);
    }

    @PutMapping("/{idCarga}")
    public ResponseEntity<CargaDeCombustibleResponseDTO> updateCargaDeCombustible(@PathVariable Long idCarga, @Valid @RequestBody CargaDeCombustibleUpdateDTO unaCargaDeCombustible) {
        CargaDeCombustibleResponseDTO cargaDeCombustibleActualizado = this.cargaDeCombustibleService.updateCarga(idCarga, unaCargaDeCombustible);

        return ResponseEntity.ok(cargaDeCombustibleActualizado);
    }

    @PostMapping()
    public ResponseEntity<CargaDeCombustibleResponseDTO> createCargaDeCombustible(@Valid @RequestBody CargaDeCombustibleCreateDTO unaCargaDeCombustible) {
        CargaDeCombustibleResponseDTO cargaDeCombustibleNuevo = this.cargaDeCombustibleService.saveCarga(unaCargaDeCombustible);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(cargaDeCombustibleNuevo.id()).toUri();

        return ResponseEntity.created(location).body(cargaDeCombustibleNuevo);
    }

    @DeleteMapping("/{idCarga}")
    public ResponseEntity<Void> deleteCargaDeCombustible(@PathVariable Long idCarga) {
        this.cargaDeCombustibleService.deleteCargaById(idCarga);
        return ResponseEntity.noContent().build();
    }

}
