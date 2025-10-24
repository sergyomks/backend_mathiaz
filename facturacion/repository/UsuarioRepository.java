package in.sisfacturacion.facturacion.repository;

import in.sisfacturacion.facturacion.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);
    Optional<UsuarioEntity> findByUsuarioId(String usuarioId);
}
