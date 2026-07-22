package com.hostel.management.service.impl;

import com.hostel.management.dto.request.AnnouncementRequest;
import com.hostel.management.dto.response.AnnouncementResponse;
import com.hostel.management.entity.Announcement;
import com.hostel.management.entity.User;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.EntityMapper;
import com.hostel.management.repository.AnnouncementRepository;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public AnnouncementResponse createAnnouncement(Long postedById, AnnouncementRequest request) {
        User user = userRepository.findById(postedById)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", postedById));

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .postedBy(user)
                .targetAudience(request.getTargetAudience() != null
                        ? Announcement.TargetAudience.valueOf(request.getTargetAudience().toUpperCase())
                        : Announcement.TargetAudience.ALL)
                .expiryDate(request.getExpiryDate())
                .build();

        return entityMapper.toAnnouncementResponse(announcementRepository.save(announcement));
    }

    @Override
    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(entityMapper::toAnnouncementResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
        announcementRepository.delete(announcement);
    }
}
