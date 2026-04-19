package gestorDeVehiculosEmpresariales.controllers;

import gestorDeVehiculosEmpresariales.entities.Reserva;
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
    public ResponseEntity<List<Reserva>> getAllReservas() {
        List<Reserva> reservas = this.reservaService.findAllReservas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{idReserva}")
    public ResponseEntity<Reserva> getReservaByID(@PathVariable Long idReserva) {
        Reserva reserva = this.reservaService.findReservaById(idReserva);
        return ResponseEntity.ok(reserva);
    }

    @PostMapping
    public ResponseEntity<Reserva> createReserva(@Valid @RequestBody Reserva unaReserva) {
        Reserva reservaNueva = this.reservaService.saveReserva(unaReserva);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(reservaNueva.getId()).toUri();
        return ResponseEntity.created(location).body(reservaNueva);
    }

    @PutMapping("/{idReserva}")
    public ResponseEntity<Reserva> updateReserva(@PathVariable Long idReserva, @Valid @RequestBody Reserva unaReserva) {
        Reserva reservaActualizada = this.reservaService.updateReserva(idReserva, unaReserva);
        return ResponseEntity.ok(reservaActualizada);
    }

    @DeleteMapping("/{idReserva}")
    public ResponseEntity<Void> deleteReserva(@PathVariable Long idReserva) {
        this.reservaService.deleteReserva(idReserva);
        return ResponseEntity.noContent().build();
    }
}
