package in.sisfacturacion.facturacion.service;

import in.sisfacturacion.facturacion.entity.UsuarioEntity;
import in.sisfacturacion.facturacion.exception.AccountLockedException;
import in.sisfacturacion.facturacion.model.LoginAttempt;
import in.sisfacturacion.facturacion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio híbrido para gestión de intentos de login
 * Combina Caffeine Cache (rápido) + Base de Datos (persistente)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptService {

    private final UsuarioRepository usuarioRepository;
    private final CacheManager cacheManager;
    
    // Configuración
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MINUTES = 15;
    
    /**
     * Verifica si una cuenta está bloqueada antes de permitir el login
     * Primero consulta la caché, luego la BD si es necesario
     */
    public void checkIfAccountIsLocked(String email) {
        // 1. Consultar caché primero (ultra rápido)
        LoginAttempt cachedAttempt = getLoginAttemptFromCache(email);
        
        if (cachedAttempt != null && cachedAttempt.getIsLocked()) {
            long minutesRemaining = calculateMinutesRemaining(cachedAttempt.getLockTime());
            
            if (minutesRemaining > 0) {
                log.warn("Intento de login en cuenta bloqueada: {}", email);
                throw new AccountLockedException(
                    String.format("Cuenta bloqueada. Intente nuevamente en %d minutos.", minutesRemaining),
                    minutesRemaining
                );
            } else {
                // Han pasado 15 minutos, desbloquear
                unlockAccount(email);
            }
        }
        
        // 2. Si no está en caché, consultar BD
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            UsuarioEntity usuario = usuarioOpt.get();
            
            if (Boolean.TRUE.equals(usuario.getAccountLocked())) {
                long minutesRemaining = calculateMinutesRemaining(usuario.getLockTime());
                
                if (minutesRemaining > 0) {
                    // Cargar en caché para próximas consultas
                    updateCache(email, usuario.getFailedAttempts(), true, 
                               usuario.getLockTime().toLocalDateTime());
                    
                    throw new AccountLockedException(
                        String.format("Cuenta bloqueada. Intente nuevamente en %d minutos.", minutesRemaining),
                        minutesRemaining
                    );
                } else {
                    // Desbloquear automáticamente
                    unlockAccount(email);
                }
            }
        }
    }
    
    /**
     * Registra un intento fallido de login
     * Incrementa contador en caché y actualiza BD periódicamente
     */
    @Transactional
    public void recordFailedAttempt(String email) {
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            return; // Usuario no existe, no registrar
        }
        
        UsuarioEntity usuario = usuarioOpt.get();
        LoginAttempt cachedAttempt = getLoginAttemptFromCache(email);
        
        int currentAttempts = cachedAttempt != null ? cachedAttempt.getAttempts() : usuario.getFailedAttempts();
        int newAttempts = currentAttempts + 1;
        
        log.info("Intento fallido #{} para usuario: {}", newAttempts, email);
        
        if (newAttempts >= MAX_ATTEMPTS) {
            // BLOQUEAR CUENTA
            LocalDateTime lockTime = LocalDateTime.now();
            
            // Actualizar caché inmediatamente
            updateCache(email, newAttempts, true, lockTime);
            
            // Actualizar BD
            usuario.setFailedAttempts(newAttempts);
            usuario.setAccountLocked(true);
            usuario.setLockTime(Timestamp.valueOf(lockTime));
            usuarioRepository.save(usuario);
            
            log.warn("Cuenta bloqueada por {} intentos fallidos: {}", newAttempts, email);
        } else {
            // Solo incrementar contador
            // Actualizar caché inmediatamente (rápido)
            updateCache(email, newAttempts, false, null);
            
            // Actualizar BD cada 3 intentos o al llegar al límite (reduce escrituras)
            if (newAttempts % 3 == 0) {
                usuario.setFailedAttempts(newAttempts);
                usuarioRepository.save(usuario);
            }
        }
    }
    
    /**
     * Resetea los intentos fallidos al tener éxito en el login
     */
    @Transactional
    public void resetFailedAttempts(String email) {
        // Limpiar caché
        clearCache(email);
        
        // Resetear en BD
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            UsuarioEntity usuario = usuarioOpt.get();
            usuario.setFailedAttempts(0);
            usuario.setAccountLocked(false);
            usuario.setLockTime(null);
            usuarioRepository.save(usuario);
            
            log.info("Intentos fallidos reseteados para usuario: {}", email);
        }
    }
    
    /**
     * Desbloquea una cuenta automáticamente después de 15 minutos
     */
    @Transactional
    public void unlockAccount(String email) {
        clearCache(email);
        
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            UsuarioEntity usuario = usuarioOpt.get();
            usuario.setFailedAttempts(0);
            usuario.setAccountLocked(false);
            usuario.setLockTime(null);
            usuarioRepository.save(usuario);
            
            log.info("Cuenta desbloqueada automáticamente: {}", email);
        }
    }
    
    /**
     * Obtiene el número de intentos restantes
     */
    public int getRemainingAttempts(String email) {
        LoginAttempt cachedAttempt = getLoginAttemptFromCache(email);
        
        if (cachedAttempt != null) {
            return Math.max(0, MAX_ATTEMPTS - cachedAttempt.getAttempts());
        }
        
        Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            return Math.max(0, MAX_ATTEMPTS - usuarioOpt.get().getFailedAttempts());
        }
        
        return MAX_ATTEMPTS;
    }
    
    // ============ MÉTODOS PRIVADOS DE CACHÉ ============
    
    private LoginAttempt getLoginAttemptFromCache(String email) {
        try {
            var cache = cacheManager.getCache("loginAttempts");
            if (cache != null) {
                return cache.get(email, LoginAttempt.class);
            }
        } catch (Exception e) {
            log.error("Error al obtener de caché: {}", e.getMessage());
        }
        return null;
    }
    
    private void updateCache(String email, int attempts, boolean isLocked, LocalDateTime lockTime) {
        try {
            var cache = cacheManager.getCache("loginAttempts");
            if (cache != null) {
                LoginAttempt attempt = LoginAttempt.builder()
                        .email(email)
                        .attempts(attempts)
                        .lastAttemptTime(LocalDateTime.now())
                        .isLocked(isLocked)
                        .lockTime(lockTime)
                        .build();
                cache.put(email, attempt);
            }
        } catch (Exception e) {
            log.error("Error al actualizar caché: {}", e.getMessage());
        }
    }
    
    private void clearCache(String email) {
        try {
            var cache = cacheManager.getCache("loginAttempts");
            if (cache != null) {
                cache.evict(email);
            }
        } catch (Exception e) {
            log.error("Error al limpiar caché: {}", e.getMessage());
        }
    }
    
    private long calculateMinutesRemaining(Timestamp lockTime) {
        if (lockTime == null) {
            return 0;
        }
        return calculateMinutesRemaining(lockTime.toLocalDateTime());
    }
    
    private long calculateMinutesRemaining(LocalDateTime lockTime) {
        if (lockTime == null) {
            return 0;
        }
        
        LocalDateTime unlockTime = lockTime.plusMinutes(LOCK_TIME_MINUTES);
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(unlockTime)) {
            return 0;
        }
        
        Duration duration = Duration.between(now, unlockTime);
        return duration.toMinutes() + 1; // +1 para redondear hacia arriba
    }
}
