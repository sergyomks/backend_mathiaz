package in.sisfacturacion.facturacion.service;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import in.sisfacturacion.facturacion.io.MercadoPagoOrderResponse;

import java.math.BigDecimal;


public interface MercadoPagoService {
    MercadoPagoOrderResponse createOrder(BigDecimal amount, String currency,
                                           String description) throws MPException, MPApiException;
}
