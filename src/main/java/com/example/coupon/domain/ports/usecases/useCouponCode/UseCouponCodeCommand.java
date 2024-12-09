package com.example.coupon.domain.ports.usecases.useCouponCode;

public record UseCouponCodeCommand(String userId, String couponCode) {
}
//사용자가 사용하는거니까 음.. 어떤유저가 어떤 코드를 사용하는지만 나타내면될뜻?
