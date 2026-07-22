package com.hostel.management.service;

import com.hostel.management.dto.request.RoomAllocationRequest;
import com.hostel.management.dto.request.RoomRequest;
import com.hostel.management.dto.response.RoomResponse;

import java.util.List;

public interface RoomService {
    RoomResponse createRoom(RoomRequest request);
    RoomResponse getRoomById(Long id);
    List<RoomResponse> getAllRooms();
    List<RoomResponse> getAvailableRooms();
    RoomResponse updateRoom(Long id, RoomRequest request);
    void deleteRoom(Long id);
    void allocateRoom(RoomAllocationRequest request);
    void vacateRoom(Long studentId);
}
