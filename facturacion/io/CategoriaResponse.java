package in.sisfacturacion.facturacion.io;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
@Builder
@Data
public class CategoriaResponse {
    private String categoriaId;
    private String nombre;
    private String descripcion;
    private String color;
    private String imgUrl;
    private Timestamp creacion;
    private Timestamp actualizacion;
    private Integer items;
}
