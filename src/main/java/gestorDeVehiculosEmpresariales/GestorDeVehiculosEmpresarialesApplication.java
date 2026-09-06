package gestorDeVehiculosEmpresariales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class GestorDeVehiculosEmpresarialesApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC")); //La app corre en UTC porque el driver JDBC manda la zona del SO en el handshake, y Postgres rechaza el alias America/Buenos_Aires. Además, guardar todo en UTC es la práctica estándar.
		SpringApplication.run(GestorDeVehiculosEmpresarialesApplication.class, args);
	}

}
