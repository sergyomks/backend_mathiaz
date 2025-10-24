package in.sisfacturacion.facturacion.service.impl;


import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import in.sisfacturacion.facturacion.io.MercadoPagoOrderResponse;
import in.sisfacturacion.facturacion.service.MercadoPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MercadoPagoServiceImpl implements MercadoPagoService {
    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Override
    public MercadoPagoOrderResponse createOrder(BigDecimal amount, String currency, String description) throws MPException, MPApiException {
        MercadoPagoConfig.setAccessToken(accessToken);
        PreferenceClient client = new PreferenceClient();
        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title(description)
                .quantity(1)
                .unitPrice(amount)
                .currencyId(currency)
                .build();
        // Puedes usar description como externalReference o generar un UUID
        String externalReference = description; // O UUID.randomUUID().toString();
        PreferenceRequest request = PreferenceRequest.builder()
                .items(Collections.singletonList(item))
                .externalReference(externalReference)
                .build();
        Preference preference = client.create(request);
        return MercadoPagoOrderResponse.builder()
                .id(preference.getId())
                .entity("pedido")
                .status("pending")
                .amount(amount)
                .currency(currency)
                .createdAt(convertToDate(preference.getDateCreated()))
                .externalReference(preference.getExternalReference())
                .initPoint(preference.getInitPoint())
                .build();
    }

    private Date convertToDate(OffsetDateTime dateCreated) {
        return Date.from(dateCreated.toInstant());
    }
}