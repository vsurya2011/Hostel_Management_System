package com.hostel.management.repository;

import com.hostel.management.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByFloorId(Long floorId);
    List<Room> findByStatus(Room.RoomStatus status);
    List<Room> findByFloor_Block_Hostel_Id(Long hostelId);
}
