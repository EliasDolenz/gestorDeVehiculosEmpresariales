package gestorDeVehiculosEmpresariales.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "novedades")
@Entity
public class Novedad {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @NotNull(message = "Debe indicar a que vehiculo pertenece la novedad")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @NotNull(message = "Debe indicar que empleado envío la novedad")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @NotNull(message = "La fecha del reporte no puede ser nula")
    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte;

    @NotNull(message = "El estado de la novedad no puede ser nulo")
    @Column(name = "estado_novedad", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoNovedad estadoNovedad;

    @NotNull(message = "La urgencia de la novedad no puede ser nula")
    @Column(name = "urgencia", nullable = false)
    @Enumerated(EnumType.STRING)
    private Urgencia urgencia;
}
