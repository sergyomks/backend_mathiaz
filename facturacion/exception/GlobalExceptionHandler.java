package in.sisfacturacion.facturacion.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para respuestas consistentes
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Maneja la excepción cuando una cuenta está bloqueada
     */
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<Map<String, Object>> handleAccountLockedException(AccountLockedException ex) {
        log.warn("Intento de acceso a cuenta bloqueada: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("error", "Cuenta Bloqueada");
        response.put("message", ex.getMessage());
        response.put("minutesRemaining", ex.getMinutesRemaining());
        
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Maneja excepciones de seguridad (usuario deshabilitado, etc.)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        // No manejar ResponseStatusException aquí, dejar que Spring lo maneje
        if (ex instanceof org.springframework.web.server.ResponseStatusException) {
            throw ex;
        }
        
        log.error("Error de ejecución: {}", ex.getMessage(), ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Error Interno");
        response.put("message", ex.getMessage() != null ? ex.getMessage() : "Ha ocurrido un error inesperado");
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
