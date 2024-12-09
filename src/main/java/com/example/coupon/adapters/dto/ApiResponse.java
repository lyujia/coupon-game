package com.example.coupon.adapters.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ApiResponse <TResponse> {
    private int statusCode;
    private TResponse body;
    //success
    public static <TResponse> ApiResponse<TResponse> ok(TResponse body){
        return new ApiResponse<>(200, body);
    }
    //fail
    public static <TResponse> ApiResponse<TResponse> fail(TResponse body){
        return new ApiResponse<>(200, body);
    }
    public static <TResponse> ApiResponse<TResponse> notFound(TResponse body){
        return new ApiResponse<>(404, body);
    }
    public static <TResponse> ApiResponse<TResponse> conflict(TResponse body){
        return new ApiResponse<>(409,body);
    }
    public static <TResponse> ApiResponse<TResponse> intervalServerError(TResponse body){
        return new ApiResponse<>(500, body);
    }

}
