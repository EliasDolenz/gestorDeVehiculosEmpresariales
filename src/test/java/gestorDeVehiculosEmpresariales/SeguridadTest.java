package gestorDeVehiculosEmpresariales;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ContenedorPostgresConfig.class)
public class SeguridadTest {

    @Autowired
    private MockMvc mockMvc;

    private String obtenerToken(String email, String contrasena) throws Exception {
        String cuerpo = """
                {"email":"%s", "contrasena": "%s"}
                """.formatted(email, contrasena);

        String respuesta = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(cuerpo)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        return JsonPath.read(respuesta, "$.token");
    }

    @Test
    @DisplayName("API cerrada sin token de autenticación")
    void noPuedeAccederSinToken() throws Exception {
        mockMvc.perform(delete("/api/empresas/999")).andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("Login con credenciales incorrectas devuelve 401")
    void loginConCredencialesIncorrectasDevuelve401() throws Exception {
        String cuerpo = """
                {"email":"%s", "contrasena": "%s"}
                """.formatted("emailincorrecto@example.com", "contrasenaIncorrecta");

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(cuerpo)).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login funciona con admin, devuelve 200 y token")
    void loginFuncionaDevuelve200YToken() throws Exception {
        String cuerpo = """
                {"email":"%s", "contrasena": "%s"}
                """.formatted("admin@example.com", "admin");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    @DisplayName("Un token válido permite acceder a la API")
    void tokenValidoPermiteAcceder() throws Exception {
        String token = obtenerToken("empleado@example.com", "empleado");

        mockMvc.perform(get("/api/empresas").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Un empleado no puede eliminar porque el rol lo bloquea")
    void empleadoNoPuedeEliminar() throws Exception {
        String token = obtenerToken("empleado@example.com", "empleado");

        mockMvc.perform(delete("/api/empresas/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un admin puede eliminar porque el rol lo permite, pero llega al service y este lanza la excepción de negocio, error 404")
    void adminPuedeEliminarPeroServiceLanzaExcepcion() throws Exception {
        String token = obtenerToken("admin@example.com", "admin");

        mockMvc.perform(delete("/api/empresas/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

}