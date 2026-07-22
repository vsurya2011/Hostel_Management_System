package com.hostel.management.service.impl;

import com.hostel.management.dto.request.ComplaintReplyRequest;
import com.hostel.management.dto.request.ComplaintRequest;
import com.hostel.management.dto.response.ComplaintResponse;
import com.hostel.management.entity.Complaint;
import com.hostel.management.entity.ComplaintReply;
import com.hostel.management.entity.Student;
import com.hostel.management.entity.User;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.EntityMapper;
import com.hostel.management.repository.ComplaintRepository;
import com.hostel.management.repository.StudentRepository;
import com.hostel.management.repository.UserRepository;
import com.hostel.management.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public ComplaintResponse createComplaint(Long studentId, ComplaintRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        Complaint complaint = Complaint.builder()
                .student(student)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory() != null ? Complaint.ComplaintCategory.valueOf(request.getCategory().toUpperCase()) : Complaint.ComplaintCategory.OTHER)
                .priority(request.getPriority() != null ? Complaint.ComplaintPriority.valueOf(request.getPriority().toUpperCase()) : Complaint.ComplaintPriority.MEDIUM)
                .status(Complaint.ComplaintStatus.OPEN)
                .build();

        return entityMapper.toComplaintResponse(complaintRepository.save(complaint));
    }

    @Override
    public ComplaintResponse getComplaintById(Long id) {
        return entityMapper.toComplaintResponse(findComplaint(id));
    }

    @Override
    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepository.findAll().stream().map(entityMapper::toComplaintResponse).collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponse> getComplaintsByStudent(Long studentId) {
        return complaintRepository.findByStudentId(studentId).stream()
                .map(entityMapper::toComplaintResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComplaintResponse updateStatus(Long id, String status) {
        Complaint complaint = findComplaint(id);
        complaint.setStatus(Complaint.ComplaintStatus.valueOf(status.toUpperCase()));
        return entityMapper.toComplaintResponse(complaintRepository.save(complaint));
    }

    @Override
    @Transactional
    public ComplaintResponse addReply(Long complaintId, Long userId, ComplaintReplyRequest request) {
        Complaint complaint = findComplaint(complaintId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        ComplaintReply reply = ComplaintReply.builder()
                .complaint(complaint)
                .repliedBy(user)
                .message(request.getMessage())
                .build();
        complaint.getReplies().add(reply);

        if (complaint.getStatus() == Complaint.ComplaintStatus.OPEN) {
            complaint.setStatus(Complaint.ComplaintStatus.IN_PROGRESS);
        }

        return entityMapper.toComplaintResponse(complaintRepository.save(complaint));
    }

    private Complaint findComplaint(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", "id", id));
    }
}
