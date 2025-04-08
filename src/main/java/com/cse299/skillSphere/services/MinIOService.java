package com.cse299.skillSphere.services;

import io.minio.*;
import io.minio.errors.MinioException;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MinIOService {

    @Value("${minio.url:http://localhost:9000}")
    private String minioUrl;

    @Value("${minio.bucket.name:skillsphare}")
    private String bucketName;

    @Value("${minio.access.key:minioadmin}")
    private String accessKey;

    @Value("${minio.secret.key:minioadmin}")
    private String secretKey;

    private MinioClient minioClient;

    @PostConstruct
    private void initializeMinio() {
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
                // Define the bucket policy for public read access
                String policyJson = "{\n" +
                                    "  \"Version\": \"2012-10-17\",\n" +
                                    "  \"Statement\": [\n" +
                                    "    {\n" +
                                    "      \"Effect\": \"Allow\",\n" +
                                    "      \"Principal\": \"*\",\n" +
                                    "      \"Action\": [\n" +
                                    "        \"s3:GetObject\"\n" +
                                    "      ],\n" +
                                    "      \"Resource\": [\n" +
                                    "        \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                                    "      ]\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}";
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policyJson)
                        .build());
            }

            String randomName = UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(randomName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return randomName;

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
