package in.sisfacturacion.facturacion.service;

import in.sisfacturacion.facturacion.io.ItemRequest;
import in.sisfacturacion.facturacion.io.ItemResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    ItemResponse add(ItemRequest request, MultipartFile file);
    List<ItemResponse>fetchItems();

    void deleteItem(String itemId);

    ItemResponse update(String itemId, ItemRequest itemRequest, MultipartFile file);
}
