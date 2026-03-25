package com.lucaflix.dto.request.stripe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Long amount;
    private String currency;
    private String description;
    private String returnUrl;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}
