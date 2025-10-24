package in.sisfacturacion.facturacion.service;

import in.sisfacturacion.facturacion.io.CategoriaRequest;
import in.sisfacturacion.facturacion.io.CategoriaResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoriaService {
    CategoriaResponse add(CategoriaRequest request, MultipartFile file);
    CategoriaResponse update(String categoriaId, CategoriaRequest request, MultipartFile file);
    List<CategoriaResponse>read();
    void delete(String categoriaId);
}
