package com.wanderly.common.util;

import com.wanderly.common.dto.CustomResponse;

public class ResponseFactory {
    public static <T> CustomResponse<T> success(String message, T data) {
        return new CustomResponse<>("success", message, data, null);
    }

    public static <T> CustomResponse<T> error(String message, T data) {
        return new CustomResponse<>("error", message, data, null);
    }

    public static <T> CustomResponse<T> errorJwt(String message, T data) {
        return new CustomResponse<>("error-jwt", message, data, null);
    }

    public static <T> CustomResponse<T> errorCustom(String message, T data) {
        return new CustomResponse<>("error-custom", message, null, null);
    }
}
