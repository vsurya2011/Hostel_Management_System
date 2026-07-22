package com.hostel.management.service.impl;

import com.hostel.management.dto.request.HostelRequest;
import com.hostel.management.dto.response.HostelResponse;
import com.hostel.management.entity.Hostel;
import com.hostel.management.entity.Staff;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.EntityMapper;
import com.hostel.management.repository.HostelRepository;
import com.hostel.management.repository.StaffRepository;
import com.hostel.management.service.HostelService;
import com.hostel.management.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HostelServiceImpl implements HostelService {

    private final HostelRepository hostelRepository;
    private final StaffRepository staffRepository;
    private final NotificationService notificationService;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public HostelResponse createHostel(HostelRequest request) {
        Hostel hostel = Hostel.builder()
                .name(request.getName())
                .address(request.getAddress())
                .type(request.getType() != null ? Hostel.HostelType.valueOf(request.getType().toUpperCase()) : null)
                .totalCapacity(request.getTotalCapacity())
                .warden(resolveWarden(request.getWardenId()))
                .build();
        return entityMapper.toHostelResponse(hostelRepository.save(hostel));
    }

    @Override
    public HostelResponse getHostelById(Long id) {
        return entityMapper.toHostelResponse(findHostel(id));
    }

    @Override
    public List<HostelResponse> getAllHostels() {
        return hostelRepository.findAll().stream().map(entityMapper::toHostelResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HostelResponse updateHostel(Long id, HostelRequest request) {
        Hostel hostel = findHostel(id);
        hostel.setName(request.getName());
        hostel.setAddress(request.getAddress());
        if (request.getType() != null) {
            hostel.setType(Hostel.HostelType.valueOf(request.getType().toUpperCase()));
        }
        hostel.setTotalCapacity(request.getTotalCapacity());
        if (request.getWardenId() != null) {
            hostel.setWarden(resolveWarden(request.getWardenId()));
        }
        return entityMapper.toHostelResponse(hostelRepository.save(hostel));
    }

    @Override
    @Transactional
    public void deleteHostel(Long id) {
        hostelRepository.delete(findHostel(id));
    }

    @Override
    @Transactional
    public HostelResponse assignWarden(Long hostelId, Long staffId) {
        Hostel hostel = findHostel(hostelId);
        Staff warden = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));
        hostel.setWarden(warden);
        Hostel saved = hostelRepository.save(hostel);

        if (warden.getUser() != null) {
            notificationService.createNotification(
                    warden.getUser().getId(),
                    "Hostel assignment",
                    "You have been assigned as warden of " + hostel.getName() + ". "
                            + "You can now manage its rooms, attendance, and complaints.",
                    "INFO");
        }

        return entityMapper.toHostelResponse(saved);
    }

    @Override
    @Transactional
    public HostelResponse unassignWarden(Long hostelId) {
        Hostel hostel = findHostel(hostelId);
        hostel.setWarden(null);
        return entityMapper.toHostelResponse(hostelRepository.save(hostel));
    }

    private Hostel findHostel(Long id) {
        return hostelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hostel", "id", id));
    }

    private Staff resolveWarden(Long wardenId) {
        if (wardenId == null) return null;
        return staffRepository.findById(wardenId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", wardenId));
    }
}
