package in.sisfacturacion.facturacion.repository;

import in.sisfacturacion.facturacion.entity.OrderEntity;
import in.sisfacturacion.facturacion.io.ProductoMasVendidoResponse;
import in.sisfacturacion.facturacion.io.VentasPorCategoriaResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity>findByOrderId(String orderId);
    List<OrderEntity> findAllByOrderByFechaCreacionDesc();
    @Query("SELECT SUM(o.grandTotal) FROM OrderEntity o WHERE DATE(o.fechaCreacion) = :date")
    Double sumVentasByDate(@Param("date")LocalDate date);
    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE DATE(o.fechaCreacion) = :date")
    Long countVentasByDate(@Param("date")LocalDate date);
    @Query("SELECT o FROM OrderEntity o ORDER BY o.fechaCreacion DESC")
    List<OrderEntity> findRecentOrders(Pageable pageable);
    @Query("""
        SELECT new in.sisfacturacion.facturacion.io.ProductoMasVendidoResponse(
            i.itemId, 
            i.nombre, 
            CAST(SUM(i.quantity) AS long),
            SUM(i.precio * i.quantity)
        )
        FROM OrderEntity o
        JOIN o.items i
        GROUP BY i.itemId, i.nombre
        ORDER BY SUM(i.quantity) DESC
    """)
    List<ProductoMasVendidoResponse> findProductosMasVendidos(Pageable pageable);

    // Ventas por categoría
    @Query("""
        SELECT new in.sisfacturacion.facturacion.io.VentasPorCategoriaResponse(
            cat.categoriaId,
            cat.nombre,
            SUM(oi.precio * oi.quantity),
            COUNT(DISTINCT oi.itemId)
        )
        FROM OrderEntity o
        JOIN o.items oi
        JOIN ItemEntity item ON item.itemId = oi.itemId
        JOIN item.categoria cat
        GROUP BY cat.categoriaId, cat.nombre
        ORDER BY SUM(oi.precio * oi.quantity) DESC
    """)
    List<VentasPorCategoriaResponse> findVentasPorCategoria();

    // Ventas optimizadas por rango de fechas (para últimos 14 días)
    @Query("""
        SELECT DATE(o.fechaCreacion) as fecha, SUM(o.grandTotal) as total
        FROM OrderEntity o
        WHERE DATE(o.fechaCreacion) BETWEEN :fechaInicio AND :fechaFin
        GROUP BY DATE(o.fechaCreacion)
        ORDER BY DATE(o.fechaCreacion)
    """)
    List<Object[]> sumVentasByDateRange(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    // Ventas por mes y año (para comparación anual)
    @Query("""
        SELECT YEAR(o.fechaCreacion) as ano, MONTH(o.fechaCreacion) as mes, SUM(o.grandTotal) as total
        FROM OrderEntity o
        WHERE YEAR(o.fechaCreacion) IN (:anoActual, :anoAnterior)
        GROUP BY YEAR(o.fechaCreacion), MONTH(o.fechaCreacion)
        ORDER BY YEAR(o.fechaCreacion), MONTH(o.fechaCreacion)
    """)
    List<Object[]> findVentasPorMesYAno(@Param("anoActual") int anoActual, @Param("anoAnterior") int anoAnterior);
}
