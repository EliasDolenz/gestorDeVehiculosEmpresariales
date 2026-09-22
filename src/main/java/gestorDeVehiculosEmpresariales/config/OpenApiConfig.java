package gestorDeVehiculosEmpresariales.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("API de Gestión de Flota Vehicular")
                .description("Gestión de vehículos empresariales: asignación, reservas, usos, novedades y cargas de combustible.")
                .version("1.0.0"));
    }
}
