package com.example.coupon.domain.services;

import com.example.coupon.domain.aggregates.CouponCode;
import com.example.coupon.domain.errors.AlreadyExistsTopic;
import com.example.coupon.domain.ports.repository.CouponCodeRepository;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeResponse;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeUseCase;
import com.example.coupon.util.UniqueIdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateCouponcodeImpl implements CreateCouponCodeUseCase {

    private final CouponCodeRepository couponCodeRepository;

    @Override
    public CreateCouponCodeResponse createCoupon(CreateCouponCodeCommand command) throws AlreadyExistsTopic {
        String couponId = UniqueIdGenerator.generateId();
        if(couponCodeRepository.findByTopic(command.topic()).isPresent()){
            throw new AlreadyExistsTopic();
        }

        couponCodeRepository.save(new CouponCode(couponId,command.topic(),command.expiredAt(), command.num()));

        return new CreateCouponCodeResponse("쿠폰을 성공적으로 생성하였습니다.");
    }
}
