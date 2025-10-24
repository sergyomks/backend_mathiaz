package in.sisfacturacion.facturacion.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.sisfacturacion.facturacion.io.CategoriaRequest;
import in.sisfacturacion.facturacion.io.CategoriaResponse;
import in.sisfacturacion.facturacion.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaService categoriaService;
    @PostMapping("/admin/categorias")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse addCategoria(@RequestPart("categoria") String categoriaString,
                                          @RequestPart("file") MultipartFile file) {
        ObjectMapper objectMapper= new ObjectMapper();
        CategoriaRequest request=null;
        try{
            request= objectMapper.readValue(categoriaString, CategoriaRequest.class);
            return categoriaService.add(request,file);
        }catch (JsonProcessingException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Error al cargar el categoria"+ex.getMessage());
        }

    }
    // para actualizar una categoria
    @PutMapping("/admin/categorias/{categoriaId}")
    public CategoriaResponse actualizarCategoria(
            @PathVariable String categoriaId,
            @RequestPart("categoria") String categoriaString,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        CategoriaRequest request;
        try {
            request = objectMapper.readValue(categoriaString, CategoriaRequest.class);
            return categoriaService.update(categoriaId, request, file);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al actualizar la categoría: " + ex.getMessage());
        }
    }
    @GetMapping("/categorias")
    public List<CategoriaResponse>listarCategoria() {
        return categoriaService.read();
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("admin/categorias/{categoriaId}")
    public void eliminarCategoria(@PathVariable String categoriaId) {
        try {
            categoriaService.delete(categoriaId);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
