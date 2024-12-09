package com.example.coupon.adapters.api;

import com.example.coupon.adapters.dto.ApiResponse;
import com.example.coupon.adapters.dto.CreateCouponCodeRequest;
import com.example.coupon.adapters.dto.DisableCouponCodeRequest;
import com.example.coupon.adapters.dto.UseCouponCodeRequest;
import com.example.coupon.domain.errors.*;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeResponse;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeUseCase;
import com.example.coupon.domain.ports.usecases.disableCouponCode.DisableCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.disableCouponCode.DisableCouponCodeResponse;
import com.example.coupon.domain.ports.usecases.disableCouponCode.DisableCouponCodeUseCase;
import com.example.coupon.domain.ports.usecases.useCouponCode.UseCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.useCouponCode.UseCouponCodeResponse;
import com.example.coupon.domain.ports.usecases.useCouponCode.UseCouponCodeUseCase;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/coupon")
@AllArgsConstructor
public class CouponCodeApiController {

    private final CreateCouponCodeUseCase createCouponCodeUseCase;
    private final UseCouponCodeUseCase useCouponCodeUseCase;
    private final DisableCouponCodeUseCase disableCouponCodeUseCase;
    @PostMapping
    public ApiResponse<CreateCouponCodeResponse> createCouponCode(@RequestBody CreateCouponCodeRequest request){
        try{
            return ApiResponse.ok(createCouponCodeUseCase.createCoupon(new CreateCouponCodeCommand(request.topic(), request.num(),request.expiredAt())));
        }catch (AlreadyExistsTopic e){
            return ApiResponse.fail(new CreateCouponCodeResponse("이미 존재하는 topic입니다."));
        }
    }
    //1. 쿠폰 발행
    @PutMapping("/use")
    public ApiResponse<UseCouponCodeResponse> useCoupon(@RequestHeader("Member-id") String userId, @RequestBody UseCouponCodeRequest request){
        try{
            return ApiResponse.ok(useCouponCodeUseCase.useCoupon(new UseCouponCodeCommand(userId, request.couponId())));
        }catch (AlreadyUsedCoupon e){
            return ApiResponse.conflict(new UseCouponCodeResponse("이미 사용한 쿠폰코드입니다."));
        }catch (NotExistsCouponCode e){
            return ApiResponse.notFound(new UseCouponCodeResponse("존재하지 않는 쿠폰코드입니다."));
        }catch(CouponInactiveException e){
            return ApiResponse.fail(new UseCouponCodeResponse("쿠폰이 소진되었거나 비활성 상태입니다."));
        }catch(MaxRetryCouponUseException e){
            return ApiResponse.intervalServerError(new UseCouponCodeResponse("쿠폰 사용이 여러번 충돌되었습니다"));
        }
    }
    //2. 쿠폰 사용(redeem)

    @PutMapping("/disable")
    public ApiResponse<DisableCouponCodeResponse> disableCouponCode(@RequestBody DisableCouponCodeRequest request){
        try{
            return ApiResponse.ok(disableCouponCodeUseCase.disableCouponCode(new DisableCouponCodeCommand(request.topic())));
        }catch (Exception e) {
            return ApiResponse.notFound(new DisableCouponCodeResponse("없는 쿠폰 topic 입니다."));
        }
    }
    //3. 일괄 쿠폰 정지(주제별)
}
