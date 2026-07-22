package com.hostel.management.controller;

import com.hostel.management.dto.request.RoomAllocationRequest;
import com.hostel.management.dto.request.RoomRequest;
import com.hostel.management.dto.response.RoomResponse;
import com.hostel.management.service.RoomService;
import com.hostel.management.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> create(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Room created", roomService.createRoom(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAllRooms()));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAvailable() {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAvailableRooms()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> update(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Room updated", roomService.updateRoom(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success("Room deleted", null));
    }

    @PostMapping("/allocate")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<Void>> allocate(@Valid @RequestBody RoomAllocationRequest request) {
        roomService.allocateRoom(request);
        return ResponseEntity.ok(ApiResponse.success("Room allocated", null));
    }

    @PostMapping("/vacate/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<Void>> vacate(@PathVariable Long studentId) {
        roomService.vacateRoom(studentId);
        return ResponseEntity.ok(ApiResponse.success("Room vacated", null));
    }
}
