package com.hostel.management.service;

import com.hostel.management.dto.request.PaymentRequest;
import com.hostel.management.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    PaymentResponse getPaymentById(Long id);
    List<PaymentResponse> getPaymentsByStudent(Long studentId);
    List<PaymentResponse> getAllPayments();
    PaymentResponse updateStatus(Long id, String status, String transactionId);
}
