package com.example.coupon.domain.ports.usecases.disableCouponCode;

import com.example.coupon.domain.errors.NotExistsCouponTopic;

public interface DisableCouponCodeUseCase {
    public DisableCouponCodeResponse disableCouponCode(DisableCouponCodeCommand command) throws NotExistsCouponTopic;
}//일괄정지
