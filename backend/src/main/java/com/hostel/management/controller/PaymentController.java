package com.hostel.management.controller;

import com.hostel.management.dto.request.PaymentRequest;
import com.hostel.management.dto.response.PaymentResponse;
import com.hostel.management.service.PaymentService;
import com.hostel.management.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> create(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment initiated", paymentService.createPayment(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentById(id)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> byStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsByStudent(studentId)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments()));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> updateStatus(@PathVariable Long id, @RequestParam String status,
                                                                       @RequestParam(required = false) String transactionId) {
        return ResponseEntity.ok(ApiResponse.success("Payment status updated", paymentService.updateStatus(id, status, transactionId)));
    }
}
