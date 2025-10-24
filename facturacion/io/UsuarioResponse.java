package in.sisfacturacion.facturacion.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioResponse {
    private String usuarioId;
    private String nombre;
    private String apellidos;
    private String email;
    private  String telefono;
    private Timestamp creacion;
    private Timestamp actualizacion;
    private String rol;
}
