package com.example.coupon.service;

import com.example.coupon.domain.aggregates.CouponCode;
import com.example.coupon.domain.errors.AlreadyExistsTopic;
import com.example.coupon.domain.errors.NotExistsCouponTopic;
import com.example.coupon.domain.ports.repository.CouponCodeRepository;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeResponse;
import com.example.coupon.domain.ports.usecases.disableCouponCode.DisableCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.disableCouponCode.DisableCouponCodeResponse;
import com.example.coupon.domain.services.CreateCouponcodeImpl;
import com.example.coupon.domain.services.DisableCouponImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class DisableCouponCodeService {
    @Mock
    private CouponCodeRepository couponCodeRepository;

    @InjectMocks
    private DisableCouponImpl disableCoupon;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("해당 topic을 가진 couponCode 성공적으로 정지하는 테스트")
    void setDisableCoupon(){
        String topic = "New Coupon";
        DisableCouponCodeCommand command = new DisableCouponCodeCommand(topic);

        when(couponCodeRepository.findByTopic(topic)).thenReturn(Optional.of(new CouponCode()));

        DisableCouponCodeResponse response = disableCoupon.disableCouponCode(command);

        verify(couponCodeRepository, times(1)).save(ArgumentMatchers.any(CouponCode.class));
        assertEquals("해당 쿠폰(topic)을 중지했습니다.", response.message());
    }
    @Test
    @DisplayName("해당 topic을 가진 couponCode 존재하지 않아 실패하는 테스트")
    void noncouponTopic(){
        String topic = "topic";
        when(couponCodeRepository.findByTopic(topic)).thenReturn(Optional.empty());
        //반환되는값이 null이다 그러면 정지할 것도 없다.

        assertThrows(NotExistsCouponTopic.class, () -> {
            disableCoupon.disableCouponCode(new DisableCouponCodeCommand(topic));
        });//비어있어서 exception발생 확인한다.
    }
}


