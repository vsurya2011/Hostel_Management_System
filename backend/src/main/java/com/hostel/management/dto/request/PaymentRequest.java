package com.hostel.management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {
    @NotNull
    private Long studentId;

    @NotNull
    private BigDecimal amount;

    private String paymentType;
    private String paymentMethod;
}
