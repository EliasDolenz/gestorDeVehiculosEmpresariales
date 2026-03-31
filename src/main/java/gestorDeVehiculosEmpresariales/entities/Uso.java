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
@Table(name = "usos")
@Entity
public class Uso {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El vehiculo debe estar especificado")
    @OneToOne
    @JoinColumn(name = "vehiculo_id", nullable = false, unique = true)
    private Vehiculo vehiculo;

    @NotNull(message = "El empleado debe estar especificado")
    @OneToOne
    @JoinColumn(name = "empleado_id", nullable = false, unique = true)
    private Empleado empleado;

    @NotNull(message = "La fecha de inicio debe estar indicada")
    private LocalDateTime fechaInicio;

    @NotNull(message = "Debe tener un estado de uso")
    private EstadoVehiculo estadoDeUso = EstadoVehiculo.EN_USO;

    private Boolean alertaEnviada = Boolean.FALSE;


}
