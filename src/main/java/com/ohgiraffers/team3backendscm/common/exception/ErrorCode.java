package com.ohgiraffers.team3backendscm.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400 Bad Request
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "BAD_REQUEST_001", "잘못된 요청입니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "BAD_REQUEST_002", "주문 상태가 올바르지 않습니다."),
    INVALID_MATCHING_STATUS(HttpStatus.BAD_REQUEST, "BAD_REQUEST_003", "배정 상태가 올바르지 않습니다."),

    // 401 Unauthorized
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "유효하지 않은 토큰입니다."),

    // 403 Forbidden
    ADMIN_ACCESS_DENIED(HttpStatus.FORBIDDEN, "FORBIDDEN_001", "접근 권한이 없습니다."),

    // 404 Not Found
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND_001", "주문을 찾을 수 없습니다."),
    MATCHING_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND_002", "배정 기록을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND_003", "상품을 찾을 수 없습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR_001", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
