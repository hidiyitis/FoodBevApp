package com.foodbev.FoodBevApp.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String uploadFile(MultipartFile file, String folder);
    void deleteFile(String fileUrl);
    boolean isValidImageFile(MultipartFile file);
}
