package com.hostel.management.service;

import com.hostel.management.dto.request.AnnouncementRequest;
import com.hostel.management.dto.response.AnnouncementResponse;

import java.util.List;

public interface AnnouncementService {
    AnnouncementResponse createAnnouncement(Long postedById, AnnouncementRequest request);
    List<AnnouncementResponse> getAllAnnouncements();
    void deleteAnnouncement(Long id);
}
