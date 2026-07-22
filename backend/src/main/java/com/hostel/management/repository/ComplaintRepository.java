package com.hostel.management.repository;

import com.hostel.management.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudentId(Long studentId);
    List<Complaint> findByStatus(Complaint.ComplaintStatus status);
    List<Complaint> findByCategory(Complaint.ComplaintCategory category);
}
