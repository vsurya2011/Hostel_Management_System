package com.hostel.management.service;

import com.hostel.management.dto.request.FloorRequest;
import com.hostel.management.dto.response.FloorResponse;

import java.util.List;

public interface FloorService {
    FloorResponse createFloor(FloorRequest request);
    FloorResponse getFloorById(Long id);
    List<FloorResponse> getAllFloors();
    List<FloorResponse> getFloorsByBlock(Long blockId);
    FloorResponse updateFloor(Long id, FloorRequest request);
    void deleteFloor(Long id);
}
