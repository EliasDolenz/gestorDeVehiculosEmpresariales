package gestorDeVehiculosEmpresariales.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la empresa no puede estar vacío")
    @Column(name = "nombre_empresa", nullable = false)
    private String nombre;

    @NotBlank(message = "La dirección de la empresa no puede estar vacía")
    @Column(name = "direccion_empresa", nullable = false, unique = true)
    private String direccion;

    @OneToMany(mappedBy = "empresa")
    private Set<Departamento> departamentos = new HashSet<>();

}