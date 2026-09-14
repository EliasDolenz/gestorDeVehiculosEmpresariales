package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.dto.novedad.NovedadCreateDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadResponseDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.novedad.NovedadUpdateDTO;
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
    public ResponseEntity<NovedadResponseDTO> getNovedadById(@PathVariable Long idNovedad) {
        NovedadResponseDTO novedad = this.novedadService.getNovedadById(idNovedad);
        return ResponseEntity.ok(novedad);
    }

    @GetMapping
    public ResponseEntity<List<NovedadSimpleDTO>> getAllNovedades() {
        List<NovedadSimpleDTO> novedades = this.novedadService.getNovedades();
        return ResponseEntity.ok(novedades);
    }

    @PostMapping
    public ResponseEntity<NovedadResponseDTO> createNovedad(@Valid @RequestBody NovedadCreateDTO unaNovedad) {
        NovedadResponseDTO novedadNueva = this.novedadService.saveNovedad(unaNovedad);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(novedadNueva.id()).toUri();

        return ResponseEntity.created(location).body(novedadNueva);
    }

    @PutMapping("/{idNovedad}")
    public ResponseEntity<NovedadResponseDTO> updateNovedad(@PathVariable Long idNovedad, @Valid @RequestBody NovedadUpdateDTO unaNovedad) {
        NovedadResponseDTO novedadActualizada = this.novedadService.updateNovedad(idNovedad, unaNovedad);
        return ResponseEntity.ok(novedadActualizada);
    }

    @DeleteMapping("/{idNovedad}")
    public ResponseEntity<Void> deleteNovedad(@PathVariable Long idNovedad) {
        this.novedadService.deleteNovedad(idNovedad);
        return ResponseEntity.noContent().build();
    }

}
