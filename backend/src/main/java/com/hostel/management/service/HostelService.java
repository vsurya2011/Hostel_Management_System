package com.hostel.management.service;

import com.hostel.management.dto.request.HostelRequest;
import com.hostel.management.dto.response.HostelResponse;

import java.util.List;

public interface HostelService {
    HostelResponse createHostel(HostelRequest request);
    HostelResponse getHostelById(Long id);
    List<HostelResponse> getAllHostels();
    HostelResponse updateHostel(Long id, HostelRequest request);
    void deleteHostel(Long id);

    // Admin-only convenience actions that touch just the warden assignment,
    // without requiring the full hostel payload.
    HostelResponse assignWarden(Long hostelId, Long staffId);
    HostelResponse unassignWarden(Long hostelId);
}
