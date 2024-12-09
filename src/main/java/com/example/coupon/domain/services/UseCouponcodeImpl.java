package com.example.coupon.domain.services;

import com.example.coupon.domain.aggregates.CouponCode;
import com.example.coupon.domain.aggregates.CouponLog;
import com.example.coupon.domain.errors.*;
import com.example.coupon.domain.ports.repository.CouponCodeRepository;
import com.example.coupon.domain.ports.repository.CouponLogRepository;
import com.example.coupon.domain.ports.usecases.useCouponCode.UseCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.useCouponCode.UseCouponCodeResponse;
import com.example.coupon.domain.ports.usecases.useCouponCode.UseCouponCodeUseCase;
import lombok.AllArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class UseCouponcodeImpl implements UseCouponCodeUseCase {

    private final CouponLogRepository couponLogRepository;
    private final CouponCodeRepository couponCodeRepository;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @Override
    public UseCouponCodeResponse useCoupon(UseCouponCodeCommand command) throws NotExistsCouponCode, AlreadyUsedCoupon, CouponInactiveException,MaxRetryCouponUseException {
        Optional<CouponCode> optionalCouponCode = couponCodeRepository.findById(command.couponCode());

        int retryCount = 0;
        boolean success = false;

        if(optionalCouponCode.isEmpty()){
            throw new NotExistsCouponCode();
        }

        CouponCode couponCode = optionalCouponCode.get();

        if(!couponCode.isAvailable()){
            throw new CouponInactiveException();
        }

        if(couponLogRepository.existsByCouponCodeAndUserId(command.couponCode(), command.userId())){
            throw new AlreadyUsedCoupon();
        }

        couponCode.decrementCount();
        //횟수 하나 줄인다. 0이면 상태 바뀜
        while(retryCount < 5 && !success) {
            try {
                couponCodeRepository.save(couponCode);
                success = true;
                break;
            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
        }
        if(!success){
            throw new MaxRetryCouponUseException();
        }

        couponLogRepository.save(new CouponLog(command.couponCode(),command.userId()));

        return new UseCouponCodeResponse("쿠폰을 성공적으로 사용하였습니다.");
    }
}
