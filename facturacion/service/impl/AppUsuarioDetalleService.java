package in.sisfacturacion.facturacion.service.impl;

import in.sisfacturacion.facturacion.entity.UsuarioEntity;
import in.sisfacturacion.facturacion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUsuarioDetalleService implements UserDetailsService {


    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioEntity existingUser=usuarioRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado con el email: " + email));
        return new User(existingUser.getEmail(),existingUser.getPassword(), Collections.singleton(new SimpleGrantedAuthority(existingUser.getRol())));
    }
}
