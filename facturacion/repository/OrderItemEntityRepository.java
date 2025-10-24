package in.sisfacturacion.facturacion.repository;

import in.sisfacturacion.facturacion.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemEntityRepository extends JpaRepository<OrderItemEntity, Long> {
}
