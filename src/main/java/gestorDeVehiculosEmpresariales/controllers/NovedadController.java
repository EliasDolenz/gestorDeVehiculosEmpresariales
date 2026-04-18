package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.entities.Novedad;
import gestorDeVehiculosEmpresariales.services.NovedadService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/novedades")
public class NovedadController {
    private final NovedadService novedadService;

    public NovedadController(NovedadService novedadService) {
        this.novedadService = novedadService;
    }

    @GetMapping("/{idNovedad}")
    public ResponseEntity<Novedad> getNovedadById(@PathVariable Long idNovedad) {
        Novedad novedad = this.novedadService.getNovedadById(idNovedad);
        return ResponseEntity.ok(novedad);
    }

    @GetMapping
    public ResponseEntity<List<Novedad>> getAllNovedades() {
        List<Novedad> novedades = this.novedadService.getNovedades();
        return ResponseEntity.ok(novedades);
    }

    @PostMapping
    public ResponseEntity<Novedad> createNovedad(@Valid @RequestBody Novedad unaNovedad) {
        Novedad novedadNueva = this.novedadService.saveNovedad(unaNovedad);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(novedadNueva.getId()).toUri();

        return ResponseEntity.created(location).body(novedadNueva);
    }

    @PutMapping("/{idNovedad}")
    public ResponseEntity<Novedad> updateNovedad(@PathVariable Long idNovedad, @Valid @RequestBody Novedad unaNovedad) {
        Novedad novedadActualizada = this.novedadService.updateNovedad(idNovedad, unaNovedad);
        return ResponseEntity.ok(novedadActualizada);
    }

    @DeleteMapping("/{idNovedad}")
    public ResponseEntity<Void> deleteNovedad(@PathVariable Long idNovedad) {
        this.novedadService.deleteNovedad(idNovedad);
        return ResponseEntity.noContent().build();
    }

}
