package gestorDeVehiculosEmpresariales;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(ContenedorPostgresConfig.class)
class GestorDeVehiculosEmpresarialesApplicationTests {

	@Test
	void contextLoads() {
	}

}
