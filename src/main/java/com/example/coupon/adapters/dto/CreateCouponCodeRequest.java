package com.example.coupon.adapters.dto;

import java.time.LocalDateTime;
import java.util.Optional;

public record CreateCouponCodeRequest(String topic, Optional<Integer> num, LocalDateTime expiredAt) {

}

