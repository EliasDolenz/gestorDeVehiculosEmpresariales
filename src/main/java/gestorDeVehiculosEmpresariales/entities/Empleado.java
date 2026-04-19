package gestorDeVehiculosEmpresariales.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "empleados")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Column(name = "apellido", nullable = false)
    private String apellido;

    @NotBlank(message = "El numero de telefono no puede estar vacío")
    @Column(name = "numero_telefono", nullable = false, unique = true)
    private String numeroTelefono;

    @NotNull(message = "El departamento no puede ser nulo")
    @ManyToOne
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Column(name = "correo_electronico", nullable = false, unique = true)
    private String correoElectronico;

    @NotBlank(message = "El puesto no puede estar vacío")
    @Column(name = "puesto", nullable = false)
    private String puesto;

    @NotNull(message = "Se debe saber si el empleado tiene o no tiene registro de conducir")
    @Column(name = "tiene_registro_conducir", nullable = false)
    private Boolean tieneRegistroConducir;


    @Column(name = "vencimiento_licencia")
    private LocalDate vencimientoLicencia;

    @NotBlank(message = "El pin de carga no puede estar vacio")
    @Column(name = "pin_carga", nullable = false)
    private String pinCarga;

    @NotNull(message = "La empresa del empleado no puede ser nula")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
}