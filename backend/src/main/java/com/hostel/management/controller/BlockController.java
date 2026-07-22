package com.hostel.management.controller;

import com.hostel.management.dto.request.BlockRequest;
import com.hostel.management.dto.response.BlockResponse;
import com.hostel.management.service.BlockService;
import com.hostel.management.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<BlockResponse>> create(@Valid @RequestBody BlockRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Block created", blockService.createBlock(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlockResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(blockService.getBlockById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BlockResponse>>> getAll(
            @RequestParam(required = false) Long hostelId) {
        List<BlockResponse> blocks = hostelId != null
                ? blockService.getBlocksByHostel(hostelId)
                : blockService.getAllBlocks();
        return ResponseEntity.ok(ApiResponse.success(blocks));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<BlockResponse>> update(@PathVariable Long id, @Valid @RequestBody BlockRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Block updated", blockService.updateBlock(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        blockService.deleteBlock(id);
        return ResponseEntity.ok(ApiResponse.success("Block deleted", null));
    }
}
