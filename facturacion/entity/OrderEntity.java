package in.sisfacturacion.facturacion.entity;

import in.sisfacturacion.facturacion.io.DetallePago;
import in.sisfacturacion.facturacion.io.MetodoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_pedidos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private String clienteNombre;
    private String numeroDni;
    private Double subTotal;
    private Double impuesto;
    private Double grandTotal;
    private LocalDateTime fechaCreacion;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItemEntity> items=new ArrayList<>();

    @Embedded
    private DetallePago detallePago;
    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @PrePersist
    protected  void onCreate() {
        this.orderId="ORD"+System.currentTimeMillis();
        this.fechaCreacion = LocalDateTime.now();
    }
}
