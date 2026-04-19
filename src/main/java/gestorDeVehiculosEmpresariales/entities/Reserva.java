package gestorDeVehiculosEmpresariales.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "reservas")
@Entity
public class Reserva {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Se debe indicar el vehículo que se quiere reservar")
    @ManyToOne
    @JoinColumn(name = "vehiculo_id")
    private Vehiculo vehiculo;

    @NotNull(message = "Indicar en que día y horario comienza la reserva")
    private LocalDateTime fechaDeInicio;

    @NotNull(message = "Indicar en que día y horario termina la reserva")
    private LocalDateTime fechaDeFinalizacion;

    @NotNull(message = "Se debe indicar el empleado que realiza la reserva")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;
}
