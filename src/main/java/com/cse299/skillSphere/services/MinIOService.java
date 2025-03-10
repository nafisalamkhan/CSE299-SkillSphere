package com.cse299.skillSphere.services;

import io.minio.*;
import io.minio.errors.MinioException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MinIOService {

    private final MinioClient minioClient;

    private final String bucketName = "skillsphare"; // Your MinIO bucket name
    private final String minioUrl = "http://localhost:9000"; // MinIO URL
    private final String accessKey = "accesskey"; // Your MinIO access key
    private final String secretKey = "secretkey"; // Your MinIO secret key

    // Constructor to initialize MinIO client
    public MinIOService() {
        try {
            this.minioClient = MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(accessKey, secretKey)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing MinIO client", e);
        }
    }

    // Upload a file to MinIO bucket
    public String uploadFile(MultipartFile file) {
        try {
            // Check if the bucket exists, if not, create it
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            String randomName = UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(randomName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return "%s/%s".formatted(bucketName, randomName);

        } catch (MinioException e) {
            throw new RuntimeException("MinIO error occurred: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file: " + e.getMessage(), e);
        }
    }

    // Delete a file from MinIO bucket
    public void deleteFile(String filePath) {
        try {
            // Delete the file from the MinIO bucket
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(filePath).build());
            System.out.println("File deleted successfully: " + filePath);
        } catch (MinioException e) {
            throw new RuntimeException("MinIO error occurred while deleting the file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file: " + e.getMessage(), e);
        }
    }
}
