package com.hostel.management.repository;

import com.hostel.management.entity.ComplaintReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintReplyRepository extends JpaRepository<ComplaintReply, Long> {
    List<ComplaintReply> findByComplaintId(Long complaintId);
}
