package in.sisfacturacion.facturacion.controller;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import in.sisfacturacion.facturacion.io.MercadoPagoOrderResponse;
import in.sisfacturacion.facturacion.io.OrderResponse;
import in.sisfacturacion.facturacion.io.PagoRequest;
import in.sisfacturacion.facturacion.io.VerificarPagoRequest;
import in.sisfacturacion.facturacion.service.MercadoPagoService;
import in.sisfacturacion.facturacion.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final OrderService orderService;
    private final MercadoPagoService mercadoPagoService;
    @PostMapping("/create_pedido")
    @ResponseStatus(HttpStatus.CREATED)
    public MercadoPagoOrderResponse createMercadopagoOrder(
            @RequestBody PagoRequest request) throws MPApiException, MPException {
        return mercadoPagoService.createOrder(request.getAmount(),request.getCurrency(),request.getDescription());

    }
    @PostMapping("/verificar")
    public OrderResponse verificarPago(@RequestBody VerificarPagoRequest request){
        return orderService.verificarPago(request);
    }

}
