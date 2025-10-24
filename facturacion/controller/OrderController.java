package in.sisfacturacion.facturacion.controller;

import in.sisfacturacion.facturacion.io.OrderRequest;
import in.sisfacturacion.facturacion.io.OrderResponse;
import in.sisfacturacion.facturacion.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse crearPedido(@RequestBody OrderRequest request){
        return orderService.createOrder(request);
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}")
    public void  eliminarPedidos(@PathVariable String orderId){
        orderService.deleteOrder(orderId);
    }
    @GetMapping("/ultimo_pedido")
    public List<OrderResponse> listarPedidos(){
        return orderService.getLatestOrders();
    }
}
