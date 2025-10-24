package in.sisfacturacion.facturacion.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductoMasVendidoResponse {
    private String itemId;
    private String nombre;
    private Long cantidadVendida;
    private Double montoTotal;
}
