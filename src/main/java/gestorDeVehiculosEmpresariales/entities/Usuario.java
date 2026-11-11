package gestorDeVehiculosEmpresariales.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "el nombre del usuario no puede estar en blanco")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "la contraseña del usuario no puede ser nula")
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @NotBlank(message = "el rol del usuario no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol;

}
