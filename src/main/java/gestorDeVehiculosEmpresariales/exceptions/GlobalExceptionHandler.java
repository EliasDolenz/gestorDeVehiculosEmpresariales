package gestorDeVehiculosEmpresariales.exceptions;

import gestorDeVehiculosEmpresariales.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                404,
                "Recurso no encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ReglaDeNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> manejarReglaDeNegocio(ReglaDeNegocioException ex, HttpServletRequest request) {

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                409,
                "Error de regla de negocio",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarArgumentosNoValidos(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensajes = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                400,
                "Argumentos no válidos",
                mensajes,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> manejarExcepcionGeneral(Exception ex, HttpServletRequest request) {

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                500,
                "Error interno del servidor",
                "Ocurrió un error interno en el servidor. Por favor, inténtelo de nuevo más tarde.",
                request.getRequestURI()
        );
        logger.error("Error no controlado en " + request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }


}
