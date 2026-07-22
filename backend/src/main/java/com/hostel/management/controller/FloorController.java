package com.hostel.management.controller;

import com.hostel.management.dto.request.FloorRequest;
import com.hostel.management.dto.response.FloorResponse;
import com.hostel.management.service.FloorService;
import com.hostel.management.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/floors")
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<FloorResponse>> create(@Valid @RequestBody FloorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Floor created", floorService.createFloor(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FloorResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(floorService.getFloorById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FloorResponse>>> getAll(
            @RequestParam(required = false) Long blockId) {
        List<FloorResponse> floors = blockId != null
                ? floorService.getFloorsByBlock(blockId)
                : floorService.getAllFloors();
        return ResponseEntity.ok(ApiResponse.success(floors));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<FloorResponse>> update(@PathVariable Long id, @Valid @RequestBody FloorRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Floor updated", floorService.updateFloor(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        floorService.deleteFloor(id);
        return ResponseEntity.ok(ApiResponse.success("Floor deleted", null));
    }
}
