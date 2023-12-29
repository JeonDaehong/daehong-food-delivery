package com.example.makedelivery.domain.image.controller;

import com.example.makedelivery.domain.image.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.example.makedelivery.common.constants.URIConstants.IMAGE_API_URI;

/**
 * 해당 컨트롤러는 사실 꼭 필요한 컨트롤러는 아닙니다.
 * 그러나 파일 관련된 여러 로직이
 * 정상적으로 작동 하는지를 확인하기 위해 만든 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(IMAGE_API_URI)
public class AmazonS3FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<String> uploadFile (@RequestPart MultipartFile file) throws IOException {
        String uploadFileName = fileService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.OK).body(uploadFileName);
    }

    @DeleteMapping
    private ResponseEntity<HttpStatus> deleteFile(@RequestParam String fileName) throws IOException {
        fileService.deleteFile(fileName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
