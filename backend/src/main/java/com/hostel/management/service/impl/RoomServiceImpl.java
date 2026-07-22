package com.hostel.management.service.impl;

import com.hostel.management.dto.request.RoomAllocationRequest;
import com.hostel.management.dto.request.RoomRequest;
import com.hostel.management.dto.response.RoomResponse;
import com.hostel.management.entity.Floor;
import com.hostel.management.entity.Room;
import com.hostel.management.entity.RoomAllocation;
import com.hostel.management.entity.Student;
import com.hostel.management.exception.BadRequestException;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.EntityMapper;
import com.hostel.management.repository.FloorRepository;
import com.hostel.management.repository.RoomAllocationRepository;
import com.hostel.management.repository.RoomRepository;
import com.hostel.management.repository.StudentRepository;
import com.hostel.management.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;
    private final StudentRepository studentRepository;
    private final RoomAllocationRepository roomAllocationRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new ResourceNotFoundException("Floor", "id", request.getFloorId()));
        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .floor(floor)
                .capacity(request.getCapacity())
                .occupied(0)
                .roomType(request.getRoomType() != null ? Room.RoomType.valueOf(request.getRoomType().toUpperCase()) : null)
                .rentAmount(request.getRentAmount())
                .status(Room.RoomStatus.AVAILABLE)
                .build();
        return entityMapper.toRoomResponse(roomRepository.save(room));
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        return entityMapper.toRoomResponse(findRoom(id));
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream().map(entityMapper::toRoomResponse).collect(Collectors.toList());
    }

    @Override
    public List<RoomResponse> getAvailableRooms() {
        return roomRepository.findByStatus(Room.RoomStatus.AVAILABLE).stream()
                .map(entityMapper::toRoomResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = findRoom(id);
        room.setRoomNumber(request.getRoomNumber());
        room.setCapacity(request.getCapacity());
        if (request.getRoomType() != null) {
            room.setRoomType(Room.RoomType.valueOf(request.getRoomType().toUpperCase()));
        }
        room.setRentAmount(request.getRentAmount());
        return entityMapper.toRoomResponse(roomRepository.save(room));
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        roomRepository.delete(findRoom(id));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "students", allEntries = true),
            @CacheEvict(value = "dashboard", allEntries = true)
    })
    public void allocateRoom(RoomAllocationRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
        Room room = findRoom(request.getRoomId());

        if (roomAllocationRepository.findByStudentIdAndStatus(student.getId(), RoomAllocation.AllocationStatus.ACTIVE).isPresent()) {
            throw new BadRequestException("Student already has an active room allocation");
        }
        if (room.getOccupied() >= room.getCapacity()) {
            throw new BadRequestException("Room is already at full capacity");
        }

        RoomAllocation allocation = RoomAllocation.builder()
                .student(student)
                .room(room)
                .allocatedDate(LocalDate.now())
                .status(RoomAllocation.AllocationStatus.ACTIVE)
                .build();
        roomAllocationRepository.save(allocation);

        room.setOccupied(room.getOccupied() + 1);
        room.setStatus(room.getOccupied() >= room.getCapacity() ? Room.RoomStatus.FULL : Room.RoomStatus.AVAILABLE);
        roomRepository.save(room);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "students", allEntries = true),
            @CacheEvict(value = "dashboard", allEntries = true)
    })
    public void vacateRoom(Long studentId) {
        RoomAllocation allocation = roomAllocationRepository
                .findByStudentIdAndStatus(studentId, RoomAllocation.AllocationStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active room allocation not found for student id: " + studentId));

        allocation.setStatus(RoomAllocation.AllocationStatus.VACATED);
        allocation.setVacatedDate(LocalDate.now());
        roomAllocationRepository.save(allocation);

        Room room = allocation.getRoom();
        room.setOccupied(Math.max(0, room.getOccupied() - 1));
        room.setStatus(Room.RoomStatus.AVAILABLE);
        roomRepository.save(room);
    }

    private Room findRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
    }
}
