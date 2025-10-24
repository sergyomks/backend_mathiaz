package in.sisfacturacion.facturacion.service.impl;

import in.sisfacturacion.facturacion.entity.CategoriaEntity;
import in.sisfacturacion.facturacion.entity.ItemEntity;
import in.sisfacturacion.facturacion.io.ItemRequest;
import in.sisfacturacion.facturacion.io.ItemResponse;
import in.sisfacturacion.facturacion.repository.CategoriaRepository;
import in.sisfacturacion.facturacion.repository.ItemRepository;
import in.sisfacturacion.facturacion.service.FileUploadService;
import in.sisfacturacion.facturacion.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final FileUploadService fileUploadService;
    private final CategoriaRepository categoriaRepository;
    private final ItemRepository itemRepository;
    @Override
    public ItemResponse add(ItemRequest request, MultipartFile file) {
        String imgUrl = fileUploadService.fileUpload(file);
        ItemEntity newItem=convertToEntity(request);
        CategoriaEntity existingCategoria=categoriaRepository.findByCategoriaId(request.getCategoriaId())
                .orElseThrow(()->new RuntimeException("Categoria no encontrada"+request.getCategoriaId()));
        newItem.setCategoria(existingCategoria);
        newItem.setImgUrl(imgUrl);
        newItem=itemRepository.save(newItem);
        return convertToResponse(newItem);
    }

    @Override
    public List<ItemResponse> fetchItems() {
        return itemRepository.findAll()
                .stream()
                .map(itemEntity -> convertToResponse(itemEntity))
                .collect(Collectors.toList());
    }

    private ItemResponse convertToResponse(ItemEntity newItem) {
        return ItemResponse.builder()
                .itemId(newItem.getItemId())
                .nombre(newItem.getNombre())
                .precio(newItem.getPrecio())
                .talla(newItem.getTalla())
                .categoriaId(newItem.getCategoria().getCategoriaId())
                .descripcion(newItem.getDescripcion())
                .categoriaNombre(newItem.getCategoria().getNombre())
                .imgUrl(newItem.getImgUrl())
                .fechaCreacion(newItem.getFechaCreacion())
                .fechaActualizacion(newItem.getFechaActualizacion())
                .build();
    }

    private ItemEntity convertToEntity(ItemRequest request) {
        return ItemEntity.builder()
                .itemId(UUID.randomUUID().toString())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .talla(request.getTalla())
                .build();
    }


    @Override
    public void deleteItem(String itemId) {
       ItemEntity existingItem=itemRepository.findByItemId(itemId)
                .orElseThrow(()->new RuntimeException("articulo no encontrado"+itemId));
       boolean isFileDelete = fileUploadService.deleteFile(existingItem.getImgUrl());
       if(isFileDelete) {
           itemRepository.delete(existingItem);
       }else {
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error al eliminar el articulo");
       }
    }

    @Override
    public ItemResponse update(String itemId, ItemRequest itemRequest, MultipartFile file) {
        ItemEntity existingItem = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("artículo no encontrado " + itemId));
        if (file != null && !file.isEmpty()) {
            // Elimina la imagen anterior
            fileUploadService.deleteFile(existingItem.getImgUrl());
            // Sube la nueva imagen
            String imgUrl = fileUploadService.fileUpload(file);
            existingItem.setImgUrl(imgUrl);
        }
        existingItem.setNombre(itemRequest.getNombre());
        existingItem.setDescripcion(itemRequest.getDescripcion());
        existingItem.setPrecio(itemRequest.getPrecio());
        existingItem.setTalla(itemRequest.getTalla());
        existingItem.setFechaActualizacion(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
        // Actualiza la categoría si es necesario
        if (!existingItem.getCategoria().getCategoriaId().equals(itemRequest.getCategoriaId())) {
            CategoriaEntity categoria = categoriaRepository.findByCategoriaId(itemRequest.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada " + itemRequest.getCategoriaId()));
            existingItem.setCategoria(categoria);
        }
        ItemEntity updatedItem = itemRepository.save(existingItem);
        return convertToResponse(updatedItem);
    }
}
