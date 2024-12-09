package com.example.coupon.service;

import com.example.coupon.domain.aggregates.CouponCode;
import com.example.coupon.domain.errors.AlreadyUsedCoupon;
import com.example.coupon.domain.errors.CouponInactiveException;
import com.example.coupon.domain.errors.MaxRetryCouponUseException;
import com.example.coupon.domain.errors.NotExistsCouponCode;
import com.example.coupon.domain.ports.repository.CouponCodeRepository;
import com.example.coupon.domain.ports.repository.CouponLogRepository;
import com.example.coupon.domain.ports.usecases.useCouponCode.UseCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.useCouponCode.UseCouponCodeResponse;
import com.example.coupon.domain.services.UseCouponcodeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UseCouponCodeService {
    @Mock
    private CouponCodeRepository couponCodeRepository;

    @Mock
    private CouponLogRepository couponLogRepository;

    @InjectMocks
    private UseCouponcodeImpl useCouponcodeImpl;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    @Test
    @DisplayName("쿠폰코드를 성공적으로 사용하는 테스트")
    void useCouponSuccess() {
        String userId = "123456";
        String couponCodeId = "123456789123456";

        CouponCode couponCode = mock(CouponCode.class);
        when(couponCode.isAvailable()).thenReturn(true);  // 쿠폰이 비활성화 상태가 아니어야 함.
        when(couponCodeRepository.findById(couponCodeId)).thenReturn(Optional.of(couponCode));
        when(couponLogRepository.existsByCouponCodeAndUserId(couponCodeId, userId)).thenReturn(false);

        UseCouponCodeCommand command = new UseCouponCodeCommand(userId, couponCodeId);

        UseCouponCodeResponse response = useCouponcodeImpl.useCoupon(command);

        assertEquals("쿠폰을 성공적으로 사용하였습니다.", response.message());

        verify(couponCodeRepository, times(1)).save(couponCode);
        verify(couponLogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰 코드로 실패하는 테스트")
    void useCouponNotExists() {
        String userId = "123456";
        String couponCodeId = "123456789";

        when(couponCodeRepository.findById(couponCodeId)).thenReturn(Optional.empty());

        UseCouponCodeCommand command = new UseCouponCodeCommand(userId, couponCodeId);

        // NotExistsCouponCode 예외가 발생해야 한다.
        assertThrows(NotExistsCouponCode.class, () -> {
            useCouponcodeImpl.useCoupon(command);
        });

        verify(couponCodeRepository, times(1)).findById(couponCodeId);
        verify(couponCodeRepository, never()).save(any());
        verify(couponLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 사용된 쿠폰 코드로 실패하는 테스트")
    void useCouponAlreadyUsed() {
        String userId = "123456";
        String couponCodeId = "123456789";

        CouponCode couponCode = mock(CouponCode.class);
        when(couponCode.isAvailable()).thenReturn(true);
        when(couponCodeRepository.findById(couponCodeId)).thenReturn(Optional.of(couponCode));
        when(couponLogRepository.existsByCouponCodeAndUserId(couponCodeId, userId)).thenReturn(true);

        UseCouponCodeCommand command = new UseCouponCodeCommand(userId, couponCodeId);

        // 이미 사용된 쿠폰 코드에 대해 예외가 발생해야 한다.
        assertThrows(AlreadyUsedCoupon.class, () -> {
            useCouponcodeImpl.useCoupon(command);
        });

        verify(couponCodeRepository, times(1)).findById(couponCodeId);
        verify(couponLogRepository, times(1)).existsByCouponCodeAndUserId(couponCodeId, userId);
        verify(couponLogRepository, never()).save(any());
        verify(couponCodeRepository, never()).save(any());
    }

    @Test
    @DisplayName("비활성화된 쿠폰 코드로 실패하는 테스트")
    void useCouponInactive() {
        String userId = "123456";
        String couponCodeId = "123456789";

        CouponCode couponCode = mock(CouponCode.class);
        when(couponCode.isAvailable()).thenReturn(false);  // 쿠폰이 비활성화 상태여야 한다.
        when(couponCodeRepository.findById(couponCodeId)).thenReturn(Optional.of(couponCode));

        UseCouponCodeCommand command = new UseCouponCodeCommand(userId, couponCodeId);

        // CouponInactiveException 예외가 발생해야 한다.
        assertThrows(CouponInactiveException.class, () -> {
            useCouponcodeImpl.useCoupon(command);
        });

        verify(couponCodeRepository, times(1)).findById(couponCodeId);
        verify(couponCodeRepository, never()).save(any());
        verify(couponLogRepository, never()).save(any());  // 쿠폰이 비활성화 상태이므로 로그 저장 안 됨.
    }

    @Test
    @DisplayName("최대 재시도 횟수 초과 시 실패하는 테스트")
    void useCouponMaxRetryExceeded() {
        String userId = "123456";
        String couponCodeId = "123456789";

        CouponCode couponCode = mock(CouponCode.class);
        when(couponCode.isAvailable()).thenReturn(true);
        when(couponCodeRepository.findById(couponCodeId)).thenReturn(Optional.of(couponCode));
        when(couponLogRepository.existsByCouponCodeAndUserId(couponCodeId, userId)).thenReturn(false);
        //다 통과

        doThrow(new OptimisticLockingFailureException("")).when(couponCodeRepository).save(any(CouponCode.class));

        UseCouponCodeCommand command = new UseCouponCodeCommand(userId, couponCodeId);

        assertThrows(MaxRetryCouponUseException.class, () -> {
            useCouponcodeImpl.useCoupon(command);
        });
        verify(couponCodeRepository, times(5)).save(any(CouponCode.class));  // 최대 5번 재시도
        verify(couponLogRepository, never()).save(any());  // 실패했으므로 로그 저장 안 됨.
    }
}
