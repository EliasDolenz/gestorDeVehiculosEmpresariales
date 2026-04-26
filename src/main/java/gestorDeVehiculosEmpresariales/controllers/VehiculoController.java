package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoUpdateDTO;
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
    public ResponseEntity<List<VehiculoSimpleDTO>> getAllVehiculos() {
        List<VehiculoSimpleDTO> vehiculos = this.vehiculoService.findAllVehiculos();
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/{idVehiculo}")
    public ResponseEntity<VehiculoResponseDTO> getVehiculoById(@PathVariable Long idVehiculo) {
        VehiculoResponseDTO vehiculo = this.vehiculoService.findVehiculoById(idVehiculo);
        return ResponseEntity.ok(vehiculo);
    }

    @GetMapping("/por-departamentos/{idDepartamento}")
    public ResponseEntity<List<VehiculoSimpleDTO>> getVehiculosByDepartamentoId(@PathVariable Long idDepartamento) {
        List<VehiculoSimpleDTO> vehiculos = this.vehiculoService.findVehiculosByDepartamentoId(idDepartamento);
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<VehiculoSimpleDTO>> getVehiculosDisponibles() {
        List<VehiculoSimpleDTO> vehiculos = this.vehiculoService.findVehiculosByEstado(EstadoVehiculo.DISPONIBLE);
        return ResponseEntity.ok(vehiculos);
    }

    @PostMapping
    public ResponseEntity<Vehiculo> createVehiculo(@Valid @RequestBody VehiculoCreateDTO unVehiculoDTO) {
        Vehiculo vehiculoNuevo = this.vehiculoService.saveVehiculo(unVehiculoDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(vehiculoNuevo.getId()).toUri();
        return ResponseEntity.created(location).body(vehiculoNuevo);
    }

    @PutMapping("/{idVehiculo}")
    public ResponseEntity<Vehiculo> updateVehiculo(@PathVariable Long idVehiculo, @Valid @RequestBody VehiculoUpdateDTO unVehiculoDTO) {
        Vehiculo vehiculoActualizado = this.vehiculoService.updateVehiculo(idVehiculo, unVehiculoDTO);
        return ResponseEntity.ok(vehiculoActualizado);
    }

    @DeleteMapping("/{idVehiculo}")
    public ResponseEntity<Void> deleteVehiculo(@PathVariable Long idVehiculo) {
        this.vehiculoService.deleteVehiculoById(idVehiculo);
        return ResponseEntity.noContent().build();
    }
}
