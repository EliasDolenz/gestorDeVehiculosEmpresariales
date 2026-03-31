package gestorDeVehiculosEmpresariales.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "departamentos")
public class Departamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del departamento no puede estar vacío")
    @Column(name = "nombre_departamento", nullable = false)
    private String nombre;

    @NotNull(message = "La empresa a la que pertenece el departamento no puede ser nula")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    @OneToMany(mappedBy = "departamento")
    private Set<Empleado> empleados = new HashSet<>();


}