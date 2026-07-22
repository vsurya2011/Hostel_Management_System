package com.hostel.management.repository;

import com.hostel.management.entity.RoomAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomAllocationRepository extends JpaRepository<RoomAllocation, Long> {
    List<RoomAllocation> findByStudentId(Long studentId);
    Optional<RoomAllocation> findByStudentIdAndStatus(Long studentId, RoomAllocation.AllocationStatus status);
    List<RoomAllocation> findByRoomId(Long roomId);
}
