package in.sisfacturacion.facturacion.service;

import in.sisfacturacion.facturacion.io.UsuarioRequest;
import in.sisfacturacion.facturacion.io.UsuarioResponse;

import java.util.List;

public interface UsuarioService {
    UsuarioResponse createUser(UsuarioRequest request);
    String getUserRole(String email);
    List<UsuarioResponse> readUsers();
    void deleteUser(String id);
    UsuarioResponse updateUser(String id, UsuarioRequest request);
}
