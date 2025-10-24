package in.sisfacturacion.facturacion.repository;

import in.sisfacturacion.facturacion.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
    Optional<CategoriaEntity> findByCategoriaId(String categoriaId);
}
