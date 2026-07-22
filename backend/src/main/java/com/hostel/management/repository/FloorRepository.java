package com.hostel.management.repository;

import com.hostel.management.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloorRepository extends JpaRepository<Floor, Long> {
    List<Floor> findByBlockId(Long blockId);
}
