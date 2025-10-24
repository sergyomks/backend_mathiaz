package in.sisfacturacion.facturacion.repository;

import in.sisfacturacion.facturacion.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    Optional<ItemEntity> findByItemId(String id);
    Integer countByCategoriaId(Long id);
}
