package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.entities.Empresa;
import gestorDeVehiculosEmpresariales.services.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {
    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping("/{idEmpresa}")
    public ResponseEntity<Empresa> getEmpresaByID(@PathVariable Long idEmpresa) {
        Empresa empresa = this.empresaService.findEmpresaById(idEmpresa);
        return ResponseEntity.ok(empresa);
    }

    @GetMapping
    public ResponseEntity<List<Empresa>> getAllEmpresas() {
        List<Empresa> empresas = this.empresaService.findAllEmpresa();
        return ResponseEntity.ok(empresas);
    }

    @PostMapping
    public ResponseEntity<Empresa> createEmpresa(@Valid @RequestBody Empresa unaEmpresa) {
        Empresa empresaNueva = this.empresaService.saveEmpresa(unaEmpresa);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(empresaNueva.getId()).toUri();

        return ResponseEntity.created(location).body(empresaNueva);
    }

    @PutMapping("/{idEmpresa}")
    public ResponseEntity<Empresa> updateEmpresa(@PathVariable Long idEmpresa, @Valid @RequestBody Empresa unaEmpresa) {
        Empresa empresaActualizada = this.empresaService.updateEmpresa(idEmpresa, unaEmpresa);
        return ResponseEntity.ok(empresaActualizada);
    }

    @DeleteMapping("/{idEmpresa}")
    public ResponseEntity<Void> deleteEmpresa(@PathVariable Long idEmpresa) {
        this.empresaService.deleteEmpresa(idEmpresa);
        return ResponseEntity.noContent().build();
    }
}
