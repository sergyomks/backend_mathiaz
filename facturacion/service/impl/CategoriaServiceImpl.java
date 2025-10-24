package in.sisfacturacion.facturacion.service.impl;

import in.sisfacturacion.facturacion.entity.CategoriaEntity;
import in.sisfacturacion.facturacion.io.CategoriaRequest;
import in.sisfacturacion.facturacion.io.CategoriaResponse;
import in.sisfacturacion.facturacion.repository.CategoriaRepository;
import in.sisfacturacion.facturacion.repository.ItemRepository;
import in.sisfacturacion.facturacion.service.CategoriaService;
import in.sisfacturacion.facturacion.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final FileUploadService fileUploadService;
    private final ItemRepository itemRepository;
    @Override
    public CategoriaResponse add(CategoriaRequest request, MultipartFile file) {
        //String imgUrl= fileUploadService.fileUpload(file);
        try {
            String fileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            String imgUrl = "http://localhost:8081/api/uploads/" + fileName;
            CategoriaEntity newCategoria = convertToEntity(request);
            newCategoria.setImgUrl(imgUrl);
            newCategoria = categoriaRepository.save(newCategoria);
            return convertToResponse(newCategoria);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }
    }

    @Override
    public CategoriaResponse update(String categoriaId, CategoriaRequest request, MultipartFile file) {
        CategoriaEntity existingCategoria = categoriaRepository.findByCategoriaId(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada: " + categoriaId));

        existingCategoria.setNombre(request.getNombre());
        existingCategoria.setDescripcion(request.getDescripcion());
        existingCategoria.setColor(request.getColor());

        if (file != null && !file.isEmpty()) {
            // Eliminar imagen anterior
            String oldImgUrl = existingCategoria.getImgUrl();
            if (oldImgUrl != null) {
                String oldFileName = oldImgUrl.substring(oldImgUrl.lastIndexOf("/") + 1);
                Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
                Path oldFilePath = uploadPath.resolve(oldFileName);
                try {
                    Files.deleteIfExists(oldFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Guardar nueva imagen
            try {
                String fileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
                Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);
                Path targetLocation = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                String imgUrl = "http://localhost:8081/api/uploads/" + fileName;
                existingCategoria.setImgUrl(imgUrl);
            } catch (IOException e) {
                throw new RuntimeException("Error al actualizar el archivo", e);
            }
        }

        existingCategoria = categoriaRepository.save(existingCategoria);
        return convertToResponse(existingCategoria);
    }

    @Override
    public List<CategoriaResponse> read() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaEntity->convertToResponse(categoriaEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String categoriaId) {
        CategoriaEntity existingCategoria= categoriaRepository.findByCategoriaId(categoriaId)
                .orElseThrow(()->new RuntimeException("Categoria no encontrada"+categoriaId));
        //fileUploadService.deleteFile(existingCategoria.getImgUrl());
        String imgUrl= existingCategoria.getImgUrl();
        String fileName= imgUrl.substring(imgUrl.lastIndexOf("/")+1);
        Path uploadPath= Paths.get("uploads").toAbsolutePath().normalize();
        Path filePath = uploadPath.resolve(fileName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        categoriaRepository.delete(existingCategoria);
    }

    private CategoriaResponse convertToResponse(CategoriaEntity newCategoria) {
        Integer itemCount = itemRepository.countByCategoriaId(newCategoria.getId());
        return CategoriaResponse.builder()
                .categoriaId(newCategoria.getCategoriaId())
                .nombre(newCategoria.getNombre())
                .descripcion(newCategoria.getDescripcion())
                .color(newCategoria.getColor())
                .imgUrl(newCategoria.getImgUrl())
                .creacion(newCategoria.getCreacion())
                .actualizacion(newCategoria.getActualizacion())
                .items(itemCount)
                .build();
    }

    private CategoriaEntity convertToEntity(CategoriaRequest request) {
        return CategoriaEntity.builder()
                .categoriaId(UUID.randomUUID().toString())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .color(request.getColor())
                .build();
    }
}
