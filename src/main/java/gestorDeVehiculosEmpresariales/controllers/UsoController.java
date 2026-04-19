package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.entities.Uso;
import gestorDeVehiculosEmpresariales.services.UsoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usos")
public class UsoController {
    private final UsoService usoService;

    public UsoController(UsoService usoService) {
        this.usoService = usoService;
    }

    @GetMapping("/{idUso}")
    public ResponseEntity<Uso> getUsoByID(@PathVariable Long idUso) {
        Uso uso = this.usoService.findUsoById(idUso);
        return ResponseEntity.ok(uso);
    }

    @GetMapping
    public ResponseEntity<List<Uso>> getAllUsos() {
        List<Uso> usos = this.usoService.findAllUso();

        return ResponseEntity.ok(usos);
    }

    @PostMapping
    public ResponseEntity<Uso> comenzarUso(@Valid @RequestBody Uso unUso) {
        Uso usoNuevo = this.usoService.comenzarUso(unUso);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(usoNuevo.getId()).toUri();

        return ResponseEntity.created(location).body(usoNuevo);
    }

    @PutMapping("/{idUso}/finalizar/{kmActualizados}")
    public ResponseEntity<Void> terminarUso(@PathVariable Long idUso, @PathVariable Integer kmActualizados) {
        this.usoService.terminarUso(idUso, kmActualizados);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idUso}")
    public ResponseEntity<Void> eliminarUso(@PathVariable Long idUso) {
        this.usoService.deleteUso(idUso);
        return ResponseEntity.noContent().build();
    }
}
