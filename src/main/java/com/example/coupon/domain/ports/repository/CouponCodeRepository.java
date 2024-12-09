package com.example.coupon.domain.ports.repository;

import com.example.coupon.domain.aggregates.CouponCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponCodeRepository extends JpaRepository<CouponCode, String> {

    Optional<CouponCode> findByTopic(String topic);
}
