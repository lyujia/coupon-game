package com.example.coupon.domain.ports.usecases.createCouponCode;

import com.example.coupon.domain.errors.AlreadyExistsTopic;

public interface CreateCouponCodeUseCase {
    public CreateCouponCodeResponse createCoupon(CreateCouponCodeCommand command) throws AlreadyExistsTopic;
}
//쿠폰 발행
