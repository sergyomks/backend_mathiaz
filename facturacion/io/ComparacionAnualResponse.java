package in.sisfacturacion.facturacion.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComparacionAnualResponse {
    private Integer mes; // 1-12
    private String mesNombre; // "Ene", "Feb", etc.
    private Double ventasAnoActual;
    private Double ventasAnoAnterior;
}
