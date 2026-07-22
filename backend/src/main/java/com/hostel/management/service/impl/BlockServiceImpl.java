package com.hostel.management.service.impl;

import com.hostel.management.dto.request.BlockRequest;
import com.hostel.management.dto.response.BlockResponse;
import com.hostel.management.entity.Block;
import com.hostel.management.entity.Hostel;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.repository.BlockRepository;
import com.hostel.management.repository.HostelRepository;
import com.hostel.management.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;
    private final HostelRepository hostelRepository;

    @Override
    @Transactional
    public BlockResponse createBlock(BlockRequest request) {
        Hostel hostel = findHostel(request.getHostelId());
        Block block = Block.builder()
                .name(request.getName())
                .hostel(hostel)
                .build();
        return toResponse(blockRepository.save(block));
    }

    @Override
    public BlockResponse getBlockById(Long id) {
        return toResponse(findBlock(id));
    }

    @Override
    public List<BlockResponse> getAllBlocks() {
        return blockRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<BlockResponse> getBlocksByHostel(Long hostelId) {
        return blockRepository.findByHostelId(hostelId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BlockResponse updateBlock(Long id, BlockRequest request) {
        Block block = findBlock(id);
        block.setName(request.getName());
        if (request.getHostelId() != null && !request.getHostelId().equals(block.getHostel().getId())) {
            block.setHostel(findHostel(request.getHostelId()));
        }
        return toResponse(blockRepository.save(block));
    }

    @Override
    @Transactional
    public void deleteBlock(Long id) {
        blockRepository.delete(findBlock(id));
    }

    private Block findBlock(Long id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Block", "id", id));
    }

    private Hostel findHostel(Long id) {
        return hostelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hostel", "id", id));
    }

    private BlockResponse toResponse(Block block) {
        BlockResponse dto = new BlockResponse();
        dto.setId(block.getId());
        dto.setName(block.getName());
        dto.setHostelId(block.getHostel() != null ? block.getHostel().getId() : null);
        dto.setHostelName(block.getHostel() != null ? block.getHostel().getName() : null);
        dto.setFloorCount(block.getFloors() != null ? block.getFloors().size() : 0);
        return dto;
    }
}
