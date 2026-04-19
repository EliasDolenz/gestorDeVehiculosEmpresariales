package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import gestorDeVehiculosEmpresariales.entities.Vehiculo;
import gestorDeVehiculosEmpresariales.services.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {
    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public ResponseEntity<List<Vehiculo>> getAllVehiculos() {
        List<Vehiculo> vehiculos = this.vehiculoService.findAllVehiculos();
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/{idVehiculo}")
    public ResponseEntity<Vehiculo> getVehiculoById(@PathVariable Long idVehiculo) {
        Vehiculo vehiculo = this.vehiculoService.findVehiculoById(idVehiculo);
        return ResponseEntity.ok(vehiculo);
    }

    @GetMapping("/por-departamentos/{idDepartamento}")
    public ResponseEntity<List<Vehiculo>> getVehiculosByDepartamentoId(@PathVariable Long idDepartamento) {
        List<Vehiculo> vehiculos = this.vehiculoService.findVehiculosByDepartamentoId(idDepartamento);
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Vehiculo>> getVehiculosDisponibles() {
        List<Vehiculo> vehiculos = this.vehiculoService.findVehiculosByEstado(EstadoVehiculo.DISPONIBLE);
        return ResponseEntity.ok(vehiculos);
    }

    @PostMapping
    public ResponseEntity<Vehiculo> createVehiculo(@Valid @RequestBody Vehiculo unVehiculo) {
        Vehiculo vehiculoNuevo = this.vehiculoService.saveVehiculo(unVehiculo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(vehiculoNuevo.getId()).toUri();
        return ResponseEntity.created(location).body(vehiculoNuevo);
    }

    @PutMapping("/{idVehiculo}")
    public ResponseEntity<Vehiculo> updateVehiculo(@PathVariable Long idVehiculo, @Valid @RequestBody Vehiculo unVehiculo) {
        Vehiculo vehiculoActualizado = this.vehiculoService.updateVehiculo(idVehiculo, unVehiculo);
        return ResponseEntity.ok(vehiculoActualizado);
    }

    @DeleteMapping("/{idVehiculo}")
    public ResponseEntity<Void> deleteVehiculo(@PathVariable Long idVehiculo) {
        this.vehiculoService.deleteVehiculoById(idVehiculo);
        return ResponseEntity.noContent().build();
    }
}
