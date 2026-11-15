package gestorDeVehiculosEmpresariales;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHashTest {
    @Test
    public void generarTest() {
        System.out.print(new BCryptPasswordEncoder().encode("empleado"));
    }
}
