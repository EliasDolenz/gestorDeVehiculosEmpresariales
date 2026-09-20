package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.dto.reserva.ReservaCreateDTO;
import gestorDeVehiculosEmpresariales.dto.reserva.ReservaResponseDTO;
import gestorDeVehiculosEmpresariales.dto.reserva.ReservaUpdateDTO;
import gestorDeVehiculosEmpresariales.services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> getAllReservas() {
        List<ReservaResponseDTO> reservas = this.reservaService.findAllReservas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaResponseDTO> getReservaByID(@PathVariable Long idReserva) {
        ReservaResponseDTO reserva = this.reservaService.findReservaById(idReserva);
        return ResponseEntity.ok(reserva);
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> createReserva(@Valid @RequestBody ReservaCreateDTO unaReserva) {
        ReservaResponseDTO reservaNueva = this.reservaService.saveReserva(unaReserva);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(reservaNueva.id()).toUri();
        return ResponseEntity.created(location).body(reservaNueva);
    }

    @PutMapping("/{idReserva}")
    public ResponseEntity<ReservaResponseDTO> updateReserva(@PathVariable Long idReserva, @Valid @RequestBody ReservaUpdateDTO unaReserva) {
        ReservaResponseDTO reservaActualizada = this.reservaService.updateReserva(idReserva, unaReserva);
        return ResponseEntity.ok(reservaActualizada);
    }

    @DeleteMapping("/{idReserva}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long idReserva) {
        this.reservaService.deleteReserva(idReserva);
        return ResponseEntity.noContent().build();
    }
}
