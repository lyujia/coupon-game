package com.example.coupon.service;

import com.example.coupon.domain.aggregates.CouponCode;
import com.example.coupon.domain.errors.AlreadyExistsTopic;
import com.example.coupon.domain.ports.repository.CouponCodeRepository;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeResponse;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeUseCase;
import com.example.coupon.domain.services.CreateCouponcodeImpl;
import com.example.coupon.util.UniqueIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CreateCouponCodeService {
    @Mock
    private CouponCodeRepository couponCodeRepository;

    @InjectMocks
    private CreateCouponcodeImpl createCouponcode;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("새로운 쿠폰을 성공적으로 생성하는 테스트")
    void testCreateCoupon(){
        String topic = "New Coupon";
        String couponId = "123456789123456";//16개 자리수 쿠폰아이디
        CreateCouponCodeCommand command = new CreateCouponCodeCommand(topic, Optional.of(10), LocalDateTime.now());

        when(couponCodeRepository.findByTopic(topic)).thenReturn(Optional.empty());

        CreateCouponCodeResponse response = createCouponcode.createCoupon(command);

        verify(couponCodeRepository, times(1)).save(ArgumentMatchers.any(CouponCode.class));
        assertEquals("쿠폰을 성공적으로 생성하였습니다.", response.message());
    }
    @Test
    @DisplayName("쿠폰의 topic이 중복되어 실패하는 테스트")
    void testDuplicateTopicFails(){
        String topic = "topic";
        when(couponCodeRepository.findByTopic(topic)).thenReturn(Optional.of(new CouponCode()));
        //반환되는값이 null이 아닐 경우

        assertThrows(AlreadyExistsTopic.class, () -> {
            createCouponcode.createCoupon(new CreateCouponCodeCommand(topic, Optional.of(10), LocalDateTime.now()));
        });
    }

}