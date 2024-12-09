package com.example.coupon.controller;

import com.example.coupon.adapters.api.CouponCodeApiController;
import com.example.coupon.adapters.dto.CreateCouponCodeRequest;
import com.example.coupon.adapters.dto.DisableCouponCodeRequest;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeUseCase;
import com.example.coupon.domain.ports.usecases.disableCouponCode.DisableCouponCodeUseCase;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.createCouponCode.CreateCouponCodeResponse;
import com.example.coupon.domain.ports.usecases.disableCouponCode.DisableCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.disableCouponCode.DisableCouponCodeResponse;
import com.example.coupon.domain.ports.usecases.useCouponCode.UseCouponCodeCommand;
import com.example.coupon.domain.ports.usecases.useCouponCode.UseCouponCodeResponse;
import com.example.coupon.domain.ports.usecases.useCouponCode.UseCouponCodeUseCase;
import com.example.coupon.domain.services.CreateCouponcodeImpl;
import com.example.coupon.domain.services.DisableCouponImpl;
import com.example.coupon.domain.services.UseCouponcodeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import org.springframework.test.web.servlet.MockMvc;

import javax.net.ssl.SSLEngineResult;
import java.awt.*;
import java.net.SocketOption;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@WebMvcTest(controllers = CouponCodeApiController.class)
public class CouponCodeControllerTest extends BaseDocumentTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DisableCouponCodeUseCase disableCouponCodeUseCase;
    @MockBean
    private CreateCouponCodeUseCase createCouponCodeUseCase;

    @MockBean
    private UseCouponCodeUseCase useCouponCodeUseCase;
    @InjectMocks
    private CouponCodeApiController couponCodeApiController; // 실제 컨트롤러에 mock 객체를 주입


    @DisplayName("새로운 코드 생성")
    @Test
    void createCouponCode() throws Exception {
        // 테스트용 요청 객체 생성
        CreateCouponCodeRequest createCouponCodeRequest = new CreateCouponCodeRequest("New Year Sale", Optional.of(100), LocalDateTime.now());

        // Mocking 서비스 레이어의 응답
        CreateCouponCodeResponse mockResponse = new CreateCouponCodeResponse("성공");

        // 서비스 메서드 호출을 mock
        when(createCouponCodeUseCase.createCoupon(any(CreateCouponCodeCommand.class)))
                .thenReturn(mockResponse);

        // 실제 테스트 요청 및 응답 검증
        mockMvc.perform(RestDocumentationRequestBuilders.post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                .content("{ \"topic\": \"New Year Sale\",\"expiredAt\": \"2024-12-31T23:59:59\"}") // 실제 JSON 요청
        ).andDo(document(snippetPath,
                requestFields(
                        fieldWithPath("topic").type(JsonFieldType.STRING).description("쿠폰 topic"),
                        fieldWithPath("num").type(JsonFieldType.NUMBER).description("발급 수량").optional(),
                        fieldWithPath("expiredAt").type(JsonFieldType.STRING).description("만료일")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                        fieldWithPath("body.message").type(JsonFieldType.STRING).description("결과 메시지")
                )
        ));

        // verify if the method is called
        verify(createCouponCodeUseCase).createCoupon(any(CreateCouponCodeCommand.class));
    }
    @DisplayName("쿠폰 코드 정지")
    @Test
    void disableCouponCode() throws Exception {
        // 테스트용 요청 객체 생성
        DisableCouponCodeRequest disableCouponCodeRequest = new DisableCouponCodeRequest("topic");

        // Mocking 서비스 레이어의 응답
        DisableCouponCodeResponse mockResponse = new DisableCouponCodeResponse("쿠폰이 정지되었습니다.");

        // 서비스 메서드 호출을 mock
        when(disableCouponCodeUseCase.disableCouponCode(any(DisableCouponCodeCommand.class)))
                .thenReturn(mockResponse);

        // 실제 테스트 요청 및 응답 검증
        mockMvc.perform(RestDocumentationRequestBuilders.put("/coupon/disable")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"topic\": \"topic\"}")
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(document(snippetPath,
                requestFields(
                        fieldWithPath("topic").type(JsonFieldType.STRING).description("쿠폰 topic")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                        fieldWithPath("body.message").type(JsonFieldType.STRING).description("결과 메시지")
                )
        ));

        // verify if the method is called
        verify(disableCouponCodeUseCase).disableCouponCode(any(DisableCouponCodeCommand.class));
    }
    @DisplayName("쿠폰코드 사용")
    @Test
    void useCouponSuccess() throws Exception {
        // 성공적인 응답을 mock
        UseCouponCodeResponse mockResponse = new UseCouponCodeResponse("쿠폰 사용에 성공했습니다.");

        // mock을 설정하여 성공적인 응답 반환
        when(useCouponCodeUseCase.useCoupon(any(UseCouponCodeCommand.class)))
                .thenReturn(mockResponse);

        // 실제 테스트 요청 및 응답 검증
        mockMvc.perform(RestDocumentationRequestBuilders.put("/coupon/use")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Member-id", "123456789")
                .content("{ \"couponId\": \"123456789123456\" }") // 요청 JSON
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(document(snippetPath,
                requestFields(
                        fieldWithPath("couponId").type(JsonFieldType.STRING).description("쿠폰 코드 ID")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                        fieldWithPath("body.message").type(JsonFieldType.STRING).description("결과 메시지")
                )
        ));

        verify(useCouponCodeUseCase).useCoupon(any(UseCouponCodeCommand.class)); // 서비스 메서드 호출 검증
    }

}

