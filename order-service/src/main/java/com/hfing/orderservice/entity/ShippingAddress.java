package com.hfing.orderservice.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddress {

    private String recipientName;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String streetDetail;
}