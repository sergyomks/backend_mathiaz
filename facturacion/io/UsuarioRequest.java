package in.sisfacturacion.facturacion.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioRequest {
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String password;
    private String rol;
}
