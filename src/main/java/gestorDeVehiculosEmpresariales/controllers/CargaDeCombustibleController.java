package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.entities.CargaDeCombustible;
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
    public ResponseEntity<CargaDeCombustible> getCargaDeCombustibleById(@PathVariable Long idCarga) {
        CargaDeCombustible cargaDeCombustible = this.cargaDeCombustibleService.findCargaById(idCarga);
        return ResponseEntity.ok(cargaDeCombustible);
    }

    @GetMapping
    public ResponseEntity<List<CargaDeCombustible>> getAllCargasDeCombustible() {
        List<CargaDeCombustible> cargasDeCombustible = this.cargaDeCombustibleService.findAllCargas();
        return ResponseEntity.ok(cargasDeCombustible);
    }

    @PutMapping("/{idCarga}")
    public ResponseEntity<CargaDeCombustible> updateCargaDeCombustible(@PathVariable Long idCarga, @Valid @RequestBody CargaDeCombustible unaCargaDeCombustible) {
        CargaDeCombustible cargaDeCombustibleActualizado = this.cargaDeCombustibleService.updateCarga(idCarga, unaCargaDeCombustible);

        return ResponseEntity.ok(cargaDeCombustibleActualizado);
    }

    @PostMapping()
    public ResponseEntity<CargaDeCombustible> createCargaDeCombustible(@Valid @RequestBody CargaDeCombustible unaCargaDeCombustible) {
        CargaDeCombustible cargaDeCombustibleNuevo = this.cargaDeCombustibleService.saveCarga(unaCargaDeCombustible);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(cargaDeCombustibleNuevo.getId()).toUri();

        return ResponseEntity.created(location).body(cargaDeCombustibleNuevo);
    }

    @DeleteMapping("/{idCarga}")
    public ResponseEntity<Void> deleteCargaDeCombustible(@PathVariable Long idCarga) {
        this.cargaDeCombustibleService.deleteCargaById(idCarga);
        return ResponseEntity.noContent().build();
    }

}
