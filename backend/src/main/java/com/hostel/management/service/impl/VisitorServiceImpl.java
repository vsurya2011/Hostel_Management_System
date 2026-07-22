package com.hostel.management.service.impl;

import com.hostel.management.dto.request.VisitorRequest;
import com.hostel.management.dto.response.VisitorResponse;
import com.hostel.management.entity.Student;
import com.hostel.management.entity.Visitor;
import com.hostel.management.exception.BadRequestException;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.EntityMapper;
import com.hostel.management.repository.StudentRepository;
import com.hostel.management.repository.VisitorRepository;
import com.hostel.management.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;
    private final StudentRepository studentRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public VisitorResponse checkIn(VisitorRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        Visitor visitor = Visitor.builder()
                .student(student)
                .visitorName(request.getVisitorName())
                .relation(request.getRelation())
                .phone(request.getPhone())
                .purpose(request.getPurpose())
                .checkInTime(LocalDateTime.now())
                .status(Visitor.VisitorStatus.CHECKED_IN)
                .build();

        return entityMapper.toVisitorResponse(visitorRepository.save(visitor));
    }

    @Override
    @Transactional
    public VisitorResponse checkOut(Long visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor", "id", visitorId));
        if (visitor.getStatus() == Visitor.VisitorStatus.CHECKED_OUT) {
            throw new BadRequestException("Visitor has already checked out");
        }
        visitor.setCheckOutTime(LocalDateTime.now());
        visitor.setStatus(Visitor.VisitorStatus.CHECKED_OUT);
        return entityMapper.toVisitorResponse(visitorRepository.save(visitor));
    }

    @Override
    public List<VisitorResponse> getVisitorsByStudent(Long studentId) {
        return visitorRepository.findByStudentId(studentId).stream()
                .map(entityMapper::toVisitorResponse).collect(Collectors.toList());
    }

    @Override
    public List<VisitorResponse> getActiveVisitors() {
        return visitorRepository.findByStatus(Visitor.VisitorStatus.CHECKED_IN).stream()
                .map(entityMapper::toVisitorResponse).collect(Collectors.toList());
    }
}
