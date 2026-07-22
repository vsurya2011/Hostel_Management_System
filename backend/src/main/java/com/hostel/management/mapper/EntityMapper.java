package com.hostel.management.mapper;

import com.hostel.management.dto.response.*;
import com.hostel.management.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralized, explicit entity -> response DTO mapping.
 * Kept manual (rather than pure ModelMapper) so nested/derived
 * fields (e.g. student's current room number) map correctly.
 */
@Component
public class EntityMapper {

    public StudentResponse toStudentResponse(Student student, String roomNumber) {
        StudentResponse dto = new StudentResponse();
        dto.setId(student.getId());
        dto.setUserId(student.getUser() != null ? student.getUser().getId() : null);
        dto.setRollNumber(student.getRollNumber());
        dto.setName(student.getName());
        dto.setEmail(student.getUser() != null ? student.getUser().getEmail() : null);
        dto.setPhone(student.getPhone());
        dto.setDepartment(student.getDepartment());
        dto.setYear(student.getYear());
        dto.setGuardianName(student.getGuardianName());
        dto.setGuardianPhone(student.getGuardianPhone());
        dto.setAddress(student.getAddress());
        dto.setAdmissionDate(student.getAdmissionDate());
        dto.setStatus(student.getStatus() != null ? student.getStatus().name() : null);
        dto.setRoomNumber(roomNumber);
        return dto;
    }

    public HostelResponse toHostelResponse(Hostel hostel) {
        HostelResponse dto = new HostelResponse();
        dto.setId(hostel.getId());
        dto.setName(hostel.getName());
        dto.setAddress(hostel.getAddress());
        dto.setType(hostel.getType() != null ? hostel.getType().name() : null);
        dto.setTotalCapacity(hostel.getTotalCapacity());
        dto.setWardenId(hostel.getWarden() != null ? hostel.getWarden().getId() : null);
        dto.setWardenName(hostel.getWarden() != null ? hostel.getWarden().getName() : null);
        dto.setBlockCount(hostel.getBlocks() != null ? hostel.getBlocks().size() : 0);
        return dto;
    }

    public RoomResponse toRoomResponse(Room room) {
        RoomResponse dto = new RoomResponse();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setCapacity(room.getCapacity());
        dto.setOccupied(room.getOccupied());
        dto.setRoomType(room.getRoomType() != null ? room.getRoomType().name() : null);
        dto.setStatus(room.getStatus() != null ? room.getStatus().name() : null);
        dto.setRentAmount(room.getRentAmount());
        if (room.getFloor() != null) {
            String blockName = room.getFloor().getBlock() != null ? room.getFloor().getBlock().getName() : "";
            dto.setFloorInfo(blockName + " - Floor " + room.getFloor().getFloorNumber());
        }
        return dto;
    }

    public ComplaintResponse toComplaintResponse(Complaint complaint) {
        ComplaintResponse dto = new ComplaintResponse();
        dto.setId(complaint.getId());
        dto.setStudentName(complaint.getStudent() != null ? complaint.getStudent().getName() : null);
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        dto.setCategory(complaint.getCategory() != null ? complaint.getCategory().name() : null);
        dto.setPriority(complaint.getPriority() != null ? complaint.getPriority().name() : null);
        dto.setStatus(complaint.getStatus() != null ? complaint.getStatus().name() : null);
        dto.setCreatedAt(complaint.getCreatedAt());
        if (complaint.getReplies() != null) {
            dto.setReplies(complaint.getReplies().stream().map(this::toComplaintReplyResponse).collect(Collectors.toList()));
        }
        return dto;
    }

    public ComplaintReplyResponse toComplaintReplyResponse(ComplaintReply reply) {
        ComplaintReplyResponse dto = new ComplaintReplyResponse();
        dto.setId(reply.getId());
        dto.setRepliedByName(reply.getRepliedBy() != null ? reply.getRepliedBy().getUsername() : null);
        dto.setMessage(reply.getMessage());
        dto.setCreatedAt(reply.getCreatedAt());
        return dto;
    }

    public LeaveResponse toLeaveResponse(LeaveRequest leave) {
        LeaveResponse dto = new LeaveResponse();
        dto.setId(leave.getId());
        dto.setStudentName(leave.getStudent() != null ? leave.getStudent().getName() : null);
        dto.setFromDate(leave.getFromDate());
        dto.setToDate(leave.getToDate());
        dto.setReason(leave.getReason());
        dto.setStatus(leave.getStatus() != null ? leave.getStatus().name() : null);
        dto.setApprovedByName(leave.getApprovedBy() != null ? leave.getApprovedBy().getUsername() : null);
        dto.setRemarks(leave.getRemarks());
        return dto;
    }

    public PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse dto = new PaymentResponse();
        dto.setId(payment.getId());
        dto.setStudentName(payment.getStudent() != null ? payment.getStudent().getName() : null);
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymentType() != null ? payment.getPaymentType().name() : null);
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setStatus(payment.getStatus() != null ? payment.getStatus().name() : null);
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentMethod(payment.getPaymentMethod());
        return dto;
    }

    public AttendanceResponse toAttendanceResponse(Attendance attendance) {
        AttendanceResponse dto = new AttendanceResponse();
        dto.setId(attendance.getId());
        dto.setStudentName(attendance.getStudent() != null ? attendance.getStudent().getName() : null);
        dto.setDate(attendance.getDate());
        dto.setStatus(attendance.getStatus() != null ? attendance.getStatus().name() : null);
        dto.setMarkedByName(attendance.getMarkedBy() != null ? attendance.getMarkedBy().getUsername() : null);
        return dto;
    }

    public VisitorResponse toVisitorResponse(Visitor visitor) {
        VisitorResponse dto = new VisitorResponse();
        dto.setId(visitor.getId());
        dto.setStudentName(visitor.getStudent() != null ? visitor.getStudent().getName() : null);
        dto.setVisitorName(visitor.getVisitorName());
        dto.setRelation(visitor.getRelation());
        dto.setPhone(visitor.getPhone());
        dto.setPurpose(visitor.getPurpose());
        dto.setCheckInTime(visitor.getCheckInTime());
        dto.setCheckOutTime(visitor.getCheckOutTime());
        dto.setStatus(visitor.getStatus() != null ? visitor.getStatus().name() : null);
        return dto;
    }

    public AnnouncementResponse toAnnouncementResponse(Announcement announcement) {
        AnnouncementResponse dto = new AnnouncementResponse();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setPostedByName(announcement.getPostedBy() != null ? announcement.getPostedBy().getUsername() : null);
        dto.setTargetAudience(announcement.getTargetAudience() != null ? announcement.getTargetAudience().name() : null);
        dto.setCreatedAt(announcement.getCreatedAt());
        dto.setExpiryDate(announcement.getExpiryDate());
        return dto;
    }

    public NotificationResponse toNotificationResponse(Notification notification) {
        NotificationResponse dto = new NotificationResponse();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType() != null ? notification.getType().name() : null);
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    public <E, R> List<R> toList(List<E> entities, java.util.function.Function<E, R> mapperFn) {
        return entities.stream().map(mapperFn).collect(Collectors.toList());
    }
}
