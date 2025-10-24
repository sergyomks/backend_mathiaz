package in.sisfacturacion.facturacion.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemResponse {
    private String itemId;
    private String nombre;
    private BigDecimal precio;
    private String talla;
    private String categoriaId;
    private String descripcion;
    private String categoriaNombre;
    private String imgUrl;
    private Timestamp fechaCreacion;
    private Timestamp fechaActualizacion;
}
