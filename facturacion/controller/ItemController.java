package in.sisfacturacion.facturacion.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.sisfacturacion.facturacion.io.ItemRequest;
import in.sisfacturacion.facturacion.io.ItemResponse;
import in.sisfacturacion.facturacion.service.ItemService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@RestController
@RequiredArgsConstructor
public class ItemController {
   private final ItemService itemService;
   @ResponseStatus(HttpStatus.CREATED)
@PostMapping("/admin/items")
    public ItemResponse agregarItem(@RequestPart("item") String itemString,
                                @RequestPart("file") MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        ItemRequest itemRequest = null;
        try {
            itemRequest = objectMapper.readValue(itemString, ItemRequest.class);
            return itemService.add(itemRequest, file);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al procesar la solicitud JSON");
        }

    }
    @GetMapping("/items")
    public List<ItemResponse> listarItems() {
        return itemService.fetchItems();
    }


    @PutMapping("/admin/items/{itemId}")
    public ItemResponse actualizarItem(
            @PathVariable String itemId,
            @RequestPart("item") String itemString,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        ItemRequest itemRequest=null;
        try {
            itemRequest = objectMapper.readValue(itemString, ItemRequest.class);
            return itemService.update(itemId, itemRequest, file);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "error al procesar la solicitud json");
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/items/{itemId}")
    public void eliminarItem(@PathVariable String itemId){
        try{
            itemService.deleteItem(itemId);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "articulo no encontrado con ID: ");
        }
    }

}