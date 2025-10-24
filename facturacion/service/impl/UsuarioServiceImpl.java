package in.sisfacturacion.facturacion.service.impl;

import in.sisfacturacion.facturacion.entity.UsuarioEntity;
import in.sisfacturacion.facturacion.io.UsuarioRequest;
import in.sisfacturacion.facturacion.io.UsuarioResponse;
import in.sisfacturacion.facturacion.repository.UsuarioRepository;
import in.sisfacturacion.facturacion.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UsuarioResponse createUser(UsuarioRequest request) {
        UsuarioEntity newUser = convertToEntity(request);
        newUser=usuarioRepository.save(newUser);
        return convertToResponse(newUser);
    }
    private UsuarioEntity convertToEntity(UsuarioRequest request) {
        return UsuarioEntity.builder()
                .usuarioId(UUID.randomUUID().toString())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol().toUpperCase())
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .telefono(request.getTelefono())
                .build();
    }
    private UsuarioResponse convertToResponse(UsuarioEntity newUser) {
        return UsuarioResponse.builder()
                .nombre(newUser.getNombre())
                .apellidos(newUser.getApellidos())
                .email(newUser.getEmail())
                .telefono(newUser.getTelefono())
                .usuarioId(newUser.getUsuarioId())
                .creacion(newUser.getCreacion())
                .actualizacion(newUser.getActualizacion())
                .rol(newUser.getRol())
                .build();
    }


    @Override
    public String getUserRole(String email) {
        UsuarioEntity existingUser=usuarioRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado con el email: " + email));
        return existingUser.getRol();
    }

    @Override
    public List<UsuarioResponse> readUsers() {
        return usuarioRepository.findAll()
                .stream()
                .map(user-> convertToResponse(user))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(String id) {
        UsuarioEntity existingUser = usuarioRepository.findByUsuarioId(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el ID: "));
        usuarioRepository.delete(existingUser);
    }

    @Override
    public UsuarioResponse updateUser(String id, UsuarioRequest request) {
        UsuarioEntity existingUser = usuarioRepository.findByUsuarioId(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el ID: " + id));
        
        // Actualizar campos básicos
        if (request.getNombre() != null && !request.getNombre().isEmpty()) {
            existingUser.setNombre(request.getNombre());
        }
        if (request.getApellidos() != null && !request.getApellidos().isEmpty()) {
            existingUser.setApellidos(request.getApellidos());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getTelefono() != null && !request.getTelefono().isEmpty()) {
            existingUser.setTelefono(request.getTelefono());
        }
        if (request.getRol() != null && !request.getRol().isEmpty()) {
            existingUser.setRol(request.getRol().toUpperCase());
        }
        
        // Solo actualizar la contraseña si se proporciona
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        UsuarioEntity updatedUser = usuarioRepository.save(existingUser);
        return convertToResponse(updatedUser);
    }

}
