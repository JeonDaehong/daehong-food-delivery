package com.example.makedelivery.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonS3FileService implements FileService {

    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String defaultUrl = "https://s3.amazonaws.com/";

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = generateFileName(file);
        amazonS3Client.putObject(bucketName, fileName, file.getInputStream(), getObjectMetadata(file));
        return defaultUrl + fileName;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void deleteFile(String fileName) throws IOException {
        amazonS3Client.deleteObject(bucketName, fileName);
    }

    @Override
    public String getFilePath(String fileName) {
        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    }

}
