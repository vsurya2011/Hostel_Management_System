package com.hostel.management.service;

import com.hostel.management.dto.request.BlockRequest;
import com.hostel.management.dto.response.BlockResponse;

import java.util.List;

public interface BlockService {
    BlockResponse createBlock(BlockRequest request);
    BlockResponse getBlockById(Long id);
    List<BlockResponse> getAllBlocks();
    List<BlockResponse> getBlocksByHostel(Long hostelId);
    BlockResponse updateBlock(Long id, BlockRequest request);
    void deleteBlock(Long id);
}
