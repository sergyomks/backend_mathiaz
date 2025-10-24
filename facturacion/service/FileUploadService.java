package in.sisfacturacion.facturacion.service;

import org.springframework.web.multipart.MultipartFile;



public interface FileUploadService {
    String fileUpload(MultipartFile file);
    boolean deleteFile(String imgUrl);
}
