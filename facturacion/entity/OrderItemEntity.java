package in.sisfacturacion.facturacion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_pedido_productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemId;
    private String nombre;
    private String talla;
    private Double precio;
    private Integer quantity;
}
