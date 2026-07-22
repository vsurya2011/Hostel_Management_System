package com.hostel.management.repository;

import com.hostel.management.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByHostelId(Long hostelId);
}
