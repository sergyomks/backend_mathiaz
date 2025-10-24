package in.sisfacturacion.facturacion.controller;

import in.sisfacturacion.facturacion.exception.AccountLockedException;
import in.sisfacturacion.facturacion.io.AuthRequest;
import in.sisfacturacion.facturacion.io.AuthResponse;
import in.sisfacturacion.facturacion.service.LoginAttemptService;
import in.sisfacturacion.facturacion.service.UsuarioService;
import in.sisfacturacion.facturacion.service.impl.AppUsuarioDetalleService;
import in.sisfacturacion.facturacion.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AppUsuarioDetalleService appUsuarioDetalleService;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) throws Exception {
        // 1. Verificar si la cuenta está bloqueada
        loginAttemptService.checkIfAccountIsLocked(request.getEmail());
        
        // 2. Intentar autenticar
        try {
            authenticate(request.getEmail(), request.getPassword());
            
            // 3. Si es exitoso, resetear intentos fallidos
            loginAttemptService.resetFailedAttempts(request.getEmail());
            
            // 4. Generar token JWT
            final UserDetails userDetails = appUsuarioDetalleService.loadUserByUsername(request.getEmail());
            final String jwtToken = jwtUtil.generateToken(userDetails);
            String rol = usuarioService.getUserRole(request.getEmail());
            
            return ResponseEntity.ok(new AuthResponse(request.getEmail(), jwtToken, rol));
        } catch (BadCredentialsException e) {
            // 5. Si falla, registrar intento fallido
            loginAttemptService.recordFailedAttempt(request.getEmail());
            
            // 6. Obtener intentos restantes
            int remainingAttempts = loginAttemptService.getRemainingAttempts(request.getEmail());
            
            String message = remainingAttempts > 0 
                ? String.format("Email o contraseña incorrectos. Le quedan %d intentos.", remainingAttempts)
                : "Cuenta bloqueada por múltiples intentos fallidos.";
            
            // Devolver respuesta JSON consistente
            Map<String, Object> errorResponse = Map.of(
                "message", message,
                "remainingAttempts", remainingAttempts
            );
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("Usuario deshabilitado");
        } catch (BadCredentialsException e) {
            // Re-lanzar la excepción original para que sea capturada en login()
            throw e;
        }
    }

    @PostMapping("/codificador")
    public String encodePassword(@RequestBody Map<String, String> request) {
        return passwordEncoder.encode(request.get("password"));
    }
}
