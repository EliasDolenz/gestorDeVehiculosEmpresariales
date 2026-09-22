package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.config.RespuestasDeError;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoCreateDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoResponseDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoSimpleDTO;
import gestorDeVehiculosEmpresariales.dto.vehiculo.VehiculoUpdateDTO;
import gestorDeVehiculosEmpresariales.entities.EstadoVehiculo;
import gestorDeVehiculosEmpresariales.services.VehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Vehículos", description = "Endpoints para la gestión de vehículos")
@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {
    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @Operation(summary = "Obtener todos los vehículos")@RespuestasDeError
    @GetMapping
    public ResponseEntity<List<VehiculoSimpleDTO>> getAllVehiculos() {
        List<VehiculoSimpleDTO> vehiculos = this.vehiculoService.findAllVehiculos();
        return ResponseEntity.ok(vehiculos);
    }

    @Operation(summary = "Obtener un vehículo por su ID")@RespuestasDeError
    @GetMapping("/{idVehiculo}")
    public ResponseEntity<VehiculoResponseDTO> getVehiculoById(@PathVariable Long idVehiculo) {
        VehiculoResponseDTO vehiculo = this.vehiculoService.findVehiculoById(idVehiculo);
        return ResponseEntity.ok(vehiculo);
    }

    @Operation(summary = "Obtener vehículos por ID de departamento")@RespuestasDeError
    @GetMapping("/por-departamentos/{idDepartamento}")
    public ResponseEntity<List<VehiculoSimpleDTO>> getVehiculosByDepartamentoId(@PathVariable Long idDepartamento) {
        List<VehiculoSimpleDTO> vehiculos = this.vehiculoService.findVehiculosByDepartamentoId(idDepartamento);
        return ResponseEntity.ok(vehiculos);
    }

    @Operation(summary = "Obtener vehículos disponibles")@RespuestasDeError
    @GetMapping("/disponibles")
    public ResponseEntity<List<VehiculoSimpleDTO>> getVehiculosDisponibles() {
        List<VehiculoSimpleDTO> vehiculos = this.vehiculoService.findVehiculosByEstado(EstadoVehiculo.DISPONIBLE);
        return ResponseEntity.ok(vehiculos);
    }

    @Operation(summary = "Crear un nuevo vehículo")@RespuestasDeError
    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> createVehiculo(@Valid @RequestBody VehiculoCreateDTO unVehiculoDTO) {
        VehiculoResponseDTO vehiculoNuevo = this.vehiculoService.saveVehiculo(unVehiculoDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(vehiculoNuevo.id()).toUri();
        return ResponseEntity.created(location).body(vehiculoNuevo);
    }

    @Operation(summary = "Actualizar un vehículo existente")@RespuestasDeError
    @PutMapping("/{idVehiculo}")
    public ResponseEntity<VehiculoResponseDTO> updateVehiculo(@PathVariable Long idVehiculo, @Valid @RequestBody VehiculoUpdateDTO unVehiculoDTO) {
        VehiculoResponseDTO vehiculoActualizado = this.vehiculoService.updateVehiculo(idVehiculo, unVehiculoDTO);
        return ResponseEntity.ok(vehiculoActualizado);
    }

    @Operation(summary = "Eliminar un vehículo")@RespuestasDeError
    @DeleteMapping("/{idVehiculo}")
    public ResponseEntity<Void> deleteVehiculo(@PathVariable Long idVehiculo) {
        this.vehiculoService.deleteVehiculoById(idVehiculo);
        return ResponseEntity.noContent().build();
    }
}
