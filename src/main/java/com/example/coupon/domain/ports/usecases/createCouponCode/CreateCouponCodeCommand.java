package com.example.coupon.domain.ports.usecases.createCouponCode;

import java.time.LocalDateTime;
import java.util.Optional;

public record CreateCouponCodeCommand(String topic, Optional<Integer> num, LocalDateTime expiredAt) {
}
