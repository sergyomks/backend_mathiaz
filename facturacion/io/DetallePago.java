package in.sisfacturacion.facturacion.io;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePago {
    private String mercadopagoOrderId;
    private String mercadopagoId;
    private String mercadopagoFirma;
    private EstadoPago estado;
    public enum EstadoPago{
        PENDIENTE, COMPLETADO, FALLIDO
    }
}
