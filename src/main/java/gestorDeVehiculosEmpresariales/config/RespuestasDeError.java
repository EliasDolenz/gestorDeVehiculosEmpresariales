package gestorDeVehiculosEmpresariales.config;

import gestorDeVehiculosEmpresariales.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "404", description = "El recurso no existe",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
@ApiResponse(responseCode = "409", description = "La operación viola una regla de negocio",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
public @interface RespuestasDeError {
}

