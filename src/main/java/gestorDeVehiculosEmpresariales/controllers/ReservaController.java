package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.config.RespuestasDeError;
import gestorDeVehiculosEmpresariales.dto.reserva.ReservaCreateDTO;
import gestorDeVehiculosEmpresariales.dto.reserva.ReservaResponseDTO;
import gestorDeVehiculosEmpresariales.dto.reserva.ReservaUpdateDTO;
import gestorDeVehiculosEmpresariales.services.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Reservas", description = "Endpoints para gestionar reservas de vehículos")
@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Operation(summary = "Obtener todas las reservas", description = "Devuelve una lista de todas las reservas registradas en el sistema.")@RespuestasDeError
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> getAllReservas() {
        List<ReservaResponseDTO> reservas = this.reservaService.findAllReservas();
        return ResponseEntity.ok(reservas);
    }

    @Operation(summary = "Obtener una reserva por su ID", description = "Devuelve los detalles de una reserva específica según su ID.")@RespuestasDeError
    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaResponseDTO> getReservaByID(@PathVariable Long idReserva) {
        ReservaResponseDTO reserva = this.reservaService.findReservaById(idReserva);
        return ResponseEntity.ok(reserva);
    }

    @Operation(summary = "Crear una nueva reserva", description = "Permite crear una nueva reserva para un vehículo.")@RespuestasDeError
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> createReserva(@Valid @RequestBody ReservaCreateDTO unaReserva) {
        ReservaResponseDTO reservaNueva = this.reservaService.saveReserva(unaReserva);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(reservaNueva.id()).toUri();
        return ResponseEntity.created(location).body(reservaNueva);
    }
    @Operation(summary = "Actualizar una reserva existente", description = "Permite actualizar los detalles de una reserva específica.")@RespuestasDeError
    @PutMapping("/{idReserva}")
    public ResponseEntity<ReservaResponseDTO> updateReserva(@PathVariable Long idReserva, @Valid @RequestBody ReservaUpdateDTO unaReserva) {
        ReservaResponseDTO reservaActualizada = this.reservaService.updateReserva(idReserva, unaReserva);
        return ResponseEntity.ok(reservaActualizada);
    }

    @Operation(summary = "Eliminar una reserva", description = "Permite eliminar una reserva específica según su ID.")@RespuestasDeError
    @DeleteMapping("/{idReserva}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long idReserva) {
        this.reservaService.deleteReserva(idReserva);
        return ResponseEntity.noContent().build();
    }
}
