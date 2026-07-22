package com.hostel.management.service.impl;

import com.hostel.management.dto.request.FloorRequest;
import com.hostel.management.dto.response.FloorResponse;
import com.hostel.management.entity.Block;
import com.hostel.management.entity.Floor;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.repository.BlockRepository;
import com.hostel.management.repository.FloorRepository;
import com.hostel.management.service.FloorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FloorServiceImpl implements FloorService {

    private final FloorRepository floorRepository;
    private final BlockRepository blockRepository;

    @Override
    @Transactional
    public FloorResponse createFloor(FloorRequest request) {
        Block block = findBlock(request.getBlockId());
        Floor floor = Floor.builder()
                .floorNumber(request.getFloorNumber())
                .block(block)
                .build();
        return toResponse(floorRepository.save(floor));
    }

    @Override
    public FloorResponse getFloorById(Long id) {
        return toResponse(findFloor(id));
    }

    @Override
    public List<FloorResponse> getAllFloors() {
        return floorRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<FloorResponse> getFloorsByBlock(Long blockId) {
        return floorRepository.findByBlockId(blockId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FloorResponse updateFloor(Long id, FloorRequest request) {
        Floor floor = findFloor(id);
        floor.setFloorNumber(request.getFloorNumber());
        if (request.getBlockId() != null && !request.getBlockId().equals(floor.getBlock().getId())) {
            floor.setBlock(findBlock(request.getBlockId()));
        }
        return toResponse(floorRepository.save(floor));
    }

    @Override
    @Transactional
    public void deleteFloor(Long id) {
        floorRepository.delete(findFloor(id));
    }

    private Floor findFloor(Long id) {
        return floorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Floor", "id", id));
    }

    private Block findBlock(Long id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block", "id", id));
    }

    private FloorResponse toResponse(Floor floor) {
        FloorResponse dto = new FloorResponse();
        dto.setId(floor.getId());
        dto.setFloorNumber(floor.getFloorNumber());
        dto.setBlockId(floor.getBlock() != null ? floor.getBlock().getId() : null);
        dto.setBlockName(floor.getBlock() != null ? floor.getBlock().getName() : null);
        if (floor.getBlock() != null && floor.getBlock().getHostel() != null) {
            dto.setHostelId(floor.getBlock().getHostel().getId());
            dto.setHostelName(floor.getBlock().getHostel().getName());
        }
        dto.setRoomCount(floor.getRooms() != null ? floor.getRooms().size() : 0);
        return dto;
    }
}
