package in.sisfacturacion.facturacion.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configuración de Caffeine Cache para control de intentos de login
     * - Expiración: 15 minutos después de la última escritura
     * - Tamaño máximo: 1000 entradas (para evitar consumo excesivo de memoria)
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("loginAttempts");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES) // Auto-limpieza después de 15 minutos
                .maximumSize(1000) // Máximo 1000 usuarios en caché
                .recordStats()); // Habilitar estadísticas para monitoreo
        return cacheManager;
    }
}
