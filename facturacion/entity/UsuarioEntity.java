package in.sisfacturacion.facturacion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tbl_usuarios")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String usuarioId;
    private String email;
    private String password;
    private String rol;
    private String apellidos;
    private String telefono;
    private String nombre;
    @CreationTimestamp
    private Timestamp creacion;
    @Column(updatable = false)
    @UpdateTimestamp
    private Timestamp actualizacion;
    
    // Campos para control de intentos fallidos de login
    @Column(name = "failed_attempts")
    @lombok.Builder.Default
    private Integer failedAttempts = 0;
    
    @Column(name = "account_locked")
    @lombok.Builder.Default
    private Boolean accountLocked = false;
    
    @Column(name = "lock_time")
    private Timestamp lockTime;

}
