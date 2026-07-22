package com.hostel.management.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentResponse {
    private Long id;
    private String studentName;
    private BigDecimal amount;
    private String paymentType;
    private LocalDate paymentDate;
    private String status;
    private String transactionId;
    private String paymentMethod;
}
