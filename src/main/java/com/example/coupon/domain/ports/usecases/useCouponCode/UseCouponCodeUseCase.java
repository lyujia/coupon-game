package com.example.coupon.domain.ports.usecases.useCouponCode;

import com.example.coupon.domain.errors.AlreadyUsedCoupon;
import com.example.coupon.domain.errors.CouponInactiveException;
import com.example.coupon.domain.errors.MaxRetryCouponUseException;
import com.example.coupon.domain.errors.NotExistsCouponCode;

public interface UseCouponCodeUseCase {
    public UseCouponCodeResponse useCoupon(UseCouponCodeCommand command) throws CouponInactiveException, NotExistsCouponCode, AlreadyUsedCoupon, MaxRetryCouponUseException;
}
