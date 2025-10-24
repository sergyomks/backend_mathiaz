package in.sisfacturacion.facturacion.exception;

/**
 * Excepción lanzada cuando una cuenta está bloqueada por intentos fallidos de login
 */
public class AccountLockedException extends RuntimeException {
    private final long minutesRemaining;
    
    public AccountLockedException(String message, long minutesRemaining) {
        super(message);
        this.minutesRemaining = minutesRemaining;
    }
    
    public AccountLockedException(String message) {
        super(message);
        this.minutesRemaining = 0;
    }
    
    public long getMinutesRemaining() {
        return minutesRemaining;
    }
}
