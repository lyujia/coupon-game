package com.example.coupon.domain.ports.repository;

import com.example.coupon.domain.aggregates.CouponCode;
import com.example.coupon.domain.aggregates.CouponLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponLogRepository extends JpaRepository<CouponLog, Integer> {
    boolean existsByCouponCodeAndUserId(String couponCode, String userId);
}
