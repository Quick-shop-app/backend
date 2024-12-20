package ugr.dss.quick_shop.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    private static final String UPLOAD_DIR = "public/images/";

    public static String saveImage(MultipartFile imageFile, Date createdAt) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        // Generate a unique file name
        String originalFileName = imageFile.getOriginalFilename();
        String uniqueFileName = createdAt.getTime() + "_product_" + originalFileName;

        // Create the directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save the file
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName; // Return the file name
    }

    public static void deleteImage(String fileName) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Path filePath = uploadPath.resolve(fileName);
        Files.deleteIfExists(filePath);
    }

    public static String replaceImage(MultipartFile imageFile, String oldFileName, Date createdAt) throws IOException {
        deleteImage(oldFileName);
        return saveImage(imageFile, createdAt);
    }
}
