package in.sisfacturacion.facturacion.controller;

import in.sisfacturacion.facturacion.io.UsuarioRequest;
import in.sisfacturacion.facturacion.io.UsuarioResponse;
import in.sisfacturacion.facturacion.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class UsuarioController {
    private final UsuarioService usuarioService;
    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrarUsuario(@RequestBody UsuarioRequest request) {
        try {
            return usuarioService.createUser(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error al registrar el usuario: " + e.getMessage());
        }
    }
    @GetMapping("/usuarios")
    public List<UsuarioResponse> listaUsuarios() {
        return usuarioService.readUsers();
    }
    @DeleteMapping("/usuarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void  eliminarUsuario(@PathVariable String id) {
        try{
            usuarioService.deleteUser(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error al eliminar el usuario: ");
        }
    }
    
    @PutMapping("/usuarios/{id}")
    public UsuarioResponse actualizarUsuario(@PathVariable String id, @RequestBody UsuarioRequest request) {
        try {
            return usuarioService.updateUser(id, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error al actualizar el usuario: " + e.getMessage());
        }
    }
}
