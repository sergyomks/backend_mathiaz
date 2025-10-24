package in.sisfacturacion.facturacion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "tbl_categoria")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String categoriaId;
    @Column(unique = true)
    private String nombre;
    private String descripcion;
    private String color;
    private String imgUrl;
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp creacion;
    @UpdateTimestamp
    private Timestamp actualizacion;

}
