package com.hostel.management.service.impl;

import com.hostel.management.dto.request.StudentRequest;
import com.hostel.management.dto.response.StudentResponse;
import com.hostel.management.entity.Room;
import com.hostel.management.entity.RoomAllocation;
import com.hostel.management.entity.Student;
import com.hostel.management.entity.User;
import com.hostel.management.exception.BadRequestException;
import com.hostel.management.exception.DuplicateResourceException;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.EntityMapper;
import com.hostel.management.repository.AttendanceRepository;
import com.hostel.management.repository.ComplaintRepository;
import com.hostel.management.repository.LeaveRepository;
import com.hostel.management.repository.PaymentRepository;
import com.hostel.management.repository.RoomAllocationRepository;
import com.hostel.management.repository.RoomRepository;
import com.hostel.management.repository.StudentRepository;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.repository.VisitorRepository;
import com.hostel.management.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final RoomAllocationRepository roomAllocationRepository;
    private final RoomRepository roomRepository;
    private final AttendanceRepository attendanceRepository;
    private final ComplaintRepository complaintRepository;
    private final PaymentRepository paymentRepository;
    private final LeaveRepository leaveRepository;
    private final VisitorRepository visitorRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    @CacheEvict(value = "students", allEntries = true)
    public StudentResponse createStudent(StudentRequest request) {
        if (request.getUserId() == null) {
            throw new BadRequestException("userId is required when creating a student");
        }
        if (studentRepository.existsByRollNumber(request.getRollNumber())) {
            throw new DuplicateResourceException("Student with roll number '" + request.getRollNumber() + "' already exists");
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        if (studentRepository.findByUserId(user.getId()).isPresent()) {
            throw new DuplicateResourceException("User with id '" + user.getId() + "' already has a student profile");
        }
        Student student = Student.builder()
                .user(user)
                .rollNumber(request.getRollNumber())
                .name(request.getName())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .year(request.getYear())
                .guardianName(request.getGuardianName())
                .guardianPhone(request.getGuardianPhone())
                .address(request.getAddress())
                .admissionDate(request.getAdmissionDate())
                .build();
        Student saved = studentRepository.save(student);
        return toResponse(saved);
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        return toResponse(findStudent(id));
    }

    @Override
    public StudentResponse getStudentByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "userId", userId));
        return toResponse(student);
    }

    @Override
    @Cacheable("students")
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "students", allEntries = true)
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = findStudent(id);
        student.setName(request.getName());
        student.setPhone(request.getPhone());
        student.setDepartment(request.getDepartment());
        student.setYear(request.getYear());
        student.setGuardianName(request.getGuardianName());
        student.setGuardianPhone(request.getGuardianPhone());
        student.setAddress(request.getAddress());
        Student saved = studentRepository.save(student);
        return toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "students", allEntries = true),
            @CacheEvict(value = "dashboard", allEntries = true)
    })
    public void deleteStudent(Long id) {
        Student student = findStudent(id);

        // A student can't be deleted while other tables still hold a
        // (non-nullable) foreign key pointing at them — every one of these
        // needs to be cleared first, or the DB rejects the delete outright.
        List<RoomAllocation> allocations = roomAllocationRepository.findByStudentId(id);
        for (RoomAllocation allocation : allocations) {
            if (allocation.getStatus() == RoomAllocation.AllocationStatus.ACTIVE) {
                Room room = allocation.getRoom();
                room.setOccupied(Math.max(0, room.getOccupied() - 1));
                room.setStatus(Room.RoomStatus.AVAILABLE);
                roomRepository.save(room);
            }
        }
        roomAllocationRepository.deleteAll(allocations);

        attendanceRepository.deleteAll(attendanceRepository.findByStudentId(id));
        complaintRepository.deleteAll(complaintRepository.findByStudentId(id));
        paymentRepository.deleteAll(paymentRepository.findByStudentId(id));
        leaveRepository.deleteAll(leaveRepository.findByStudentId(id));
        visitorRepository.deleteAll(visitorRepository.findByStudentId(id));

        studentRepository.delete(student);
    }

    private Student findStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    private StudentResponse toResponse(Student student) {
        String roomNumber = roomAllocationRepository
                .findByStudentIdAndStatus(student.getId(), RoomAllocation.AllocationStatus.ACTIVE)
                .map(alloc -> alloc.getRoom().getRoomNumber())
                .orElse(null);
        return entityMapper.toStudentResponse(student, roomNumber);
    }
}
