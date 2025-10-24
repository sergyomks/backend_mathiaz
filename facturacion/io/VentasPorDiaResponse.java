package in.sisfacturacion.facturacion.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentasPorDiaResponse {
    private LocalDate fecha;
    private Double ventas;
    private String fechaFormateada; // Para mostrar en el eje X del gráfico (ej: "01/12", "02/12")
}

