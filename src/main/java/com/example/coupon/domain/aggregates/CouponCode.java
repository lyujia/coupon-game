package com.example.coupon.domain.aggregates;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "coupon_code")
@NoArgsConstructor
public class CouponCode {
    @Id
    @Column(name = "coupon_code",length = 16)
    private String couponCode;

    @Column(nullable = false, unique = true)
    private String topic;

    @Column(nullable = false)
    private CouponStatus status;

    @Column(name = "issued_at",nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expired_at",nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = true)
    private Integer num;

    @Version
    private int version;

    public CouponCode(String couponCode, String topic, LocalDateTime expiredAt, Optional<Integer> num){
        this.couponCode = couponCode;
        this.topic = topic;
        this.status = CouponStatus.ACTIVE;
        this.issuedAt = LocalDateTime.now();
        this.expiredAt = expiredAt;
        if(num.isPresent()){
            this.num = num.get();
        }
    }
    public void setDisabled(){
        this.status = CouponStatus.DISABLED;
    }
    public void decrementCount(){
        this.num -= 1;
        if(this.num == 0){
            this.status = CouponStatus.EXHUUSTED;
        }
    }
    public boolean isAvailable(){
        return status.equals(CouponStatus.ACTIVE);
    }
}
