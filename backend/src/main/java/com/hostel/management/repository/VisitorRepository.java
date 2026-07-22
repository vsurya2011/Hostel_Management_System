package com.hostel.management.repository;

import com.hostel.management.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByStudentId(Long studentId);
    List<Visitor> findByStatus(Visitor.VisitorStatus status);
}
