package in.sisfacturacion.facturacion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Modelo para almacenar intentos de login en caché
 * Este objeto se almacena en Caffeine y representa el estado actual de intentos de un usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {
    private String email;
    private Integer attempts;
    private LocalDateTime lastAttemptTime;
    private Boolean isLocked;
    private LocalDateTime lockTime;
}
