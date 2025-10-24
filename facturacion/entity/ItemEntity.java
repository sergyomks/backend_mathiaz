package in.sisfacturacion.facturacion.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
@Entity
@Table (name = "tbl_productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String itemId;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private String talla;
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp fechaCreacion;
    @UpdateTimestamp
    private Timestamp fechaActualizacion;
    private String imgUrl;
    @ManyToOne
    @JoinColumn(name= "categoria_id", nullable = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private CategoriaEntity categoria;
}
