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
    @ManyToOne
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @NotNull(message = "El empleado debe estar especificado")
    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @NotNull(message = "La fecha de inicio debe estar indicada")
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFinalizacion;

    @NotNull(message = "Debe tener un estado de uso")
    @Enumerated(EnumType.STRING)
    private EstadoUso estadoDeUso = EstadoUso.INICIADO;

    //Esta alertaEnviada sirve para avisar cuando el horario está cerca de una Reserva que ya tenga el vehiculo
    private Boolean alertaEnviada = Boolean.FALSE;


}
