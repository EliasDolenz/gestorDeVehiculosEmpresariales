package gestorDeVehiculosEmpresariales.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vehiculos")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La patente no puede estar vacía")
    @Column(name = "patente", nullable = false, unique = true)
    private String patente;

    @NotBlank(message = "La marca no puede estar vacía")
    @Column(name = "marca", nullable = false)
    private String marca;

    @NotBlank(message = "El modelo no puede estar vacío")
    @Column(name = "modelo", nullable = false)
    private String modelo;

    @NotNull(message = "El kmActual no puede ser nulo")
    @Column(name = "km_actual", nullable = false)
    private Integer kmActual;

    @NotNull(message = "La fecha de service no puede ser nulo")
    @Column(name = "fecha_de_service", nullable = false)
    private LocalDate fechaDeService;

    @NotNull(message = "La fecha de vencimiento del service no puede ser nulo")
    @Column(name = "vencimiento_service", nullable = false)
    private LocalDate vencimientoService;

    @NotNull(message = "Debe indicar si la VTV se encuentra vigente o no")
    @Column(name = "vtv_vigente", nullable = false)
    private Boolean vtvVigente;

    @NotNull(message = "Debe indicar la fecha de vencimiento de la VTV")
    @Column(name = "vencimiento_vtv", nullable = false)
    private LocalDate vencimientoVtv;

    @NotNull(message = "Debe indicar a que departamento pertenece el vehiculo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @OneToMany(mappedBy = "vehiculo")
    private Set<Novedad> novedades;

    @NotNull(message = "El nivel de combustible del vehiculo debe estar indicado siempre")
    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_combustible", nullable = false)
    private Combustible nivelCombustible;

    @NotNull(message = "El estado del vehiculo debe estar indicado siempre")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_vehiculo", nullable = false)
    private EstadoVehiculo estadoVehiculo;

    @NotBlank(message = "El numero de la tarjeta no puede estar vacio")
    @Column(name = "numero_tarjeta_ypf", nullable = false)
    private String numeroTarjetaYPF;

    @OneToMany(mappedBy = "vehiculo")
    private Set<Reserva> reservas = new HashSet<>();

    @OneToMany(mappedBy = "vehiculo")
    private Set<Uso> usos = new HashSet<>();

    @OneToMany(mappedBy = "vehiculo")
    private Set<CargaDeCombustible> cargasDeCombustible = new HashSet<>();
}
