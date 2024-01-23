package com.example.makedelivery.domain.store.service;

import com.example.makedelivery.domain.image.service.FileService;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.store.model.StoreInsertRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreApplicationService {

    private final StoreService storeService;
    private final FileService fileService;

    @Transactional
    public void addStore(StoreInsertRequest request, Member member, MultipartFile file) throws IOException {
        String imageFileName = fileService.uploadFile(file); // AWS S3 upload
        storeService.addStore(request, member, imageFileName);
    }

    @Transactional
    public void updateStoreImage(Member member, Long storeId, String beforeImageName, MultipartFile file) throws IOException {
        fileService.deleteFile(beforeImageName); // AWS S3에서 기존 이미지 삭제
        String newImageName = fileService.uploadFile(file); // AWS S3에 새로운 이미지 업로드
        storeService.updateStoreImage(storeId, member, newImageName);
    }

}
