package net.devstudy.resume.ms.profile.web;

import java.util.Locale;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import net.devstudy.resume.media.internal.component.ObjectStorageClient;

@RestController
@ConditionalOnProperty(name = "upload.object-storage.enabled", havingValue = "true")
public class UploadsProxyController {

    private final ObjectStorageClient objectStorageClient;

    public UploadsProxyController(ObjectStorageClient objectStorageClient) {
        this.objectStorageClient = objectStorageClient;
    }

    @GetMapping("/uploads/photos/{fileName:.+}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String fileName) {
        return getObject("photos", fileName);
    }

    @GetMapping("/uploads/certificates/{fileName:.+}")
    public ResponseEntity<Resource> getCertificate(@PathVariable String fileName) {
        return getObject("certificates", fileName);
    }

    private ResponseEntity<Resource> getObject(String folder, String fileName) {
        if (!isSafeFileName(fileName)) {
            return ResponseEntity.notFound().build();
        }
        String objectKey = folder + "/" + fileName;
        try {
            InputStreamResource resource = new InputStreamResource(objectStorageClient.getObject(objectKey));
            return ResponseEntity.ok()
                    .contentType(resolveMediaType(fileName))
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isSafeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }
        return !fileName.contains("/") && !fileName.contains("\\") && !fileName.contains("..");
    }

    private MediaType resolveMediaType(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }
        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lower.endsWith(".webp")) {
            return MediaType.valueOf("image/webp");
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
