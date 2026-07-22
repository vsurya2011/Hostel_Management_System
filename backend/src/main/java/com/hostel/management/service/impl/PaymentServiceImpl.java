package com.hostel.management.service.impl;

import com.hostel.management.dto.request.PaymentRequest;
import com.hostel.management.dto.response.PaymentResponse;
import com.hostel.management.entity.Payment;
import com.hostel.management.entity.Student;
import com.hostel.management.exception.BadRequestException;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.mapper.EntityMapper;
import com.hostel.management.repository.PaymentRepository;
import com.hostel.management.repository.StudentRepository;
import com.hostel.management.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        Payment payment = Payment.builder()
                .student(student)
                .amount(request.getAmount())
                .paymentType(request.getPaymentType() != null ? Payment.PaymentType.valueOf(request.getPaymentType().toUpperCase()) : Payment.PaymentType.HOSTEL_FEE)
                .paymentMethod(request.getPaymentMethod())
                .paymentDate(LocalDate.now())
                .status(Payment.PaymentStatus.PENDING)
                .build();

        return entityMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        return entityMapper.toPaymentResponse(findPayment(id));
    }

    @Override
    public List<PaymentResponse> getPaymentsByStudent(Long studentId) {
        return paymentRepository.findByStudentId(studentId).stream()
                .map(entityMapper::toPaymentResponse).collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(entityMapper::toPaymentResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponse updateStatus(Long id, String status, String transactionId) {
        Payment payment = findPayment(id);

        Payment.PaymentStatus newStatus;
        try {
            newStatus = Payment.PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid payment status: " + status);
        }

        boolean isOnline = "ONLINE".equalsIgnoreCase(payment.getPaymentMethod());
        if (newStatus == Payment.PaymentStatus.SUCCESS && isOnline
                && (transactionId == null || transactionId.isBlank())) {
            throw new BadRequestException("Transaction ID is required to mark an online payment as paid");
        }

        payment.setStatus(newStatus);
        if (transactionId != null && !transactionId.isBlank()) {
            payment.setTransactionId(transactionId);
        }
        return entityMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    private Payment findPayment(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }
}
