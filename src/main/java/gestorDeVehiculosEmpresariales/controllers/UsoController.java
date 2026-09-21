package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.dto.uso.TerminarUsoDTO;
import gestorDeVehiculosEmpresariales.dto.uso.UsoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.uso.UsoResponseDTO;
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
    public ResponseEntity<UsoResponseDTO> getUsoByID(@PathVariable Long idUso) {
        UsoResponseDTO uso = this.usoService.findUsoById(idUso);
        return ResponseEntity.ok(uso);
    }

    @GetMapping
    public ResponseEntity<List<UsoResponseDTO>> getAllUsos() {
        List<UsoResponseDTO> usos = this.usoService.findAllUso();

        return ResponseEntity.ok(usos);
    }

    @PostMapping
    public ResponseEntity<UsoResponseDTO> comenzarUso(@Valid @RequestBody UsoCreateDTO unUso) {
        UsoResponseDTO usoNuevo = this.usoService.comenzarUso(unUso);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(usoNuevo.id()).toUri();

        return ResponseEntity.created(location).body(usoNuevo);
    }

    @PutMapping("/{idUso}/finalizar")
    public ResponseEntity<UsoResponseDTO> terminarUso(@PathVariable Long idUso, @Valid @RequestBody TerminarUsoDTO datosFinalizacion) {
        UsoResponseDTO usoFinalizado = this.usoService.terminarUso(idUso, datosFinalizacion);
        return ResponseEntity.ok(usoFinalizado);
    }

    @DeleteMapping("/{idUso}")
    public ResponseEntity<Void> eliminarUso(@PathVariable Long idUso) {
        this.usoService.deleteUso(idUso);
        return ResponseEntity.noContent().build();
    }
}
