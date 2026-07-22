package com.hostel.management.controller;

import com.hostel.management.service.FileService;
import com.hostel.management.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success("File uploaded", fileService.uploadFile(file)));
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String fileName) {
        fileService.deleteFile(fileName);
        return ResponseEntity.ok(ApiResponse.success("File deleted", null));
    }
}
