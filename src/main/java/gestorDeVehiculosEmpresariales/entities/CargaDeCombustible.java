package gestorDeVehiculosEmpresariales.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "carga_de_combustible")
@Entity
public class CargaDeCombustible {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Debe indicar que auto realizó la carga de combustible")
    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @NotNull(message = "Debe inidicar la cantidad de Litros que se cargaron")
    private Double cantidadLitros;

    @NotNull(message = "Debe indicar el kilometraje del auto a la hora de cargar")
    private Integer kmVehiculo;

    @NotNull(message = "Debe indicar que empleado realizó la recarga")
    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @NotNull(message = "Debe indicar en que fecha se realizó la recarga")
    private LocalDateTime fechaRecarga;
}
