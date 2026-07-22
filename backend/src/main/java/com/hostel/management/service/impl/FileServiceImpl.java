package com.hostel.management.service.impl;

import com.hostel.management.service.FileService;
import com.hostel.management.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileStorageUtil fileStorageUtil;

    @Override
    public String uploadFile(MultipartFile file) {
        return fileStorageUtil.storeFile(file);
    }

    @Override
    public void deleteFile(String fileName) {
        fileStorageUtil.deleteFile(fileName);
    }
}
