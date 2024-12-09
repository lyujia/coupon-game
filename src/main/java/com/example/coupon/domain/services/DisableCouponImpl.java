package com.example.coupon.domain.services;

import com.example.coupon.domain.aggregates.CouponCode;
import com.example.coupon.domain.errors.NotExistsCouponTopic;
import com.example.coupon.domain.ports.repository.CouponCodeRepository;
import com.example.coupon.domain.ports.usecases.disableCouponCode.DisableCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.disableCouponCode.DisableCouponCodeResponse;
import com.example.coupon.domain.ports.usecases.disableCouponCode.DisableCouponCodeUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class DisableCouponImpl implements DisableCouponCodeUseCase {
    private final CouponCodeRepository couponCodeRepository;

    @Override
    public DisableCouponCodeResponse disableCouponCode(DisableCouponCodeCommand command) throws NotExistsCouponTopic {
        Optional<CouponCode> optionalCouponCode = couponCodeRepository.findByTopic(command.topic());//findby coupontopic으로 바꿔야함
        if(optionalCouponCode.isEmpty()){
            throw new NotExistsCouponTopic();
        }
        CouponCode code = optionalCouponCode.get();
        code.setDisabled();

        couponCodeRepository.save(code);
        //상태만 disable로 바꿈
        return new DisableCouponCodeResponse("해당 쿠폰(topic)을 중지했습니다.");
    }
}
