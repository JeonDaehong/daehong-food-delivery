package com.example.makedelivery.domain.image.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * FileService 는 AWS S3를 사용할 수도 있고, 다른 방법을 사용할 수도 있습니다.
 * 그런 가능성이 있기 때문에 인터페이스로 추상화를 하였습니다.
 */
public interface FileService {

    String uploadFile(MultipartFile file) throws IOException;

    void deleteFile(String fileName) throws IOException;

    String getFilePath(String fileName);

}
