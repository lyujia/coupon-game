package com.example.coupon.domain.aggregates;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class CouponLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "user_id",nullable = false)
    private String userId;

    @Column(name = "redeemed_at",nullable = false)
    private LocalDateTime redeemedAt;
    public CouponLog(String couponCode, String userId){
        this.couponCode = couponCode;
        this.userId = userId;
        this.redeemedAt = LocalDateTime.now();
    }
}
