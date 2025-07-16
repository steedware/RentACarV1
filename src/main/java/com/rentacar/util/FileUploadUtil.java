package com.rentacar.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUploadUtil {
    
    private static final Logger logger = Logger.getLogger(FileUploadUtil.class.getName());

    /**
     * Saves uploaded file to the specified directory with a unique filename
     * 
     * @param uploadDir Directory where the file should be saved
     * @param multipartFile File to save
     * @return Filename of the saved file
     * @throws IOException If an I/O error occurs
     */
    public static String saveFile(String uploadDir, MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "unknown_file";
        }
        
        // Generate unique filename directly here instead of using a separate method
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            logger.info("Creating directory: " + uploadPath);
            Files.createDirectories(uploadPath);
        }
        
        // Save the file
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            logger.info("Saving file to: " + filePath);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not save file: " + fileName, ex);
            throw new IOException("Could not save file: " + fileName, ex);
        }
    }
    
    /**
     * Deletes a file from the specified directory
     */
    public static void deleteFile(String uploadDir, String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                logger.info("File deleted: " + filePath);
            } else {
                logger.warning("File not found: " + filePath);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error deleting file: " + filePath, ex);
            throw ex;
        }
    }
}
