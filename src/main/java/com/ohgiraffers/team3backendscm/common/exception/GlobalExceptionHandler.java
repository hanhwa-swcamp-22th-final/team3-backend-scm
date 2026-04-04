package com.ohgiraffers.team3backendscm.common.exception;

import com.ohgiraffers.team3backendscm.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** 명시적 비즈니스 예외 — ErrorCode 기반 상태 코드로 응답 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("Business exception: {}", e.getMessage());

        return ResponseEntity.status(errorCode.getStatus())
                .body(ApiResponse.failure(errorCode.getCode(), e.getMessage()));
    }

    /** 리소스 미존재 — 현재 서비스 코드가 NoSuchElementException 을 던지므로 404 로 처리 */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoSuchElementException(NoSuchElementException e) {
        log.error("Resource not found: {}", e.getMessage());

        return ResponseEntity.status(ErrorCode.ORDER_NOT_FOUND.getStatus())
                .body(ApiResponse.failure(ErrorCode.ORDER_NOT_FOUND.getCode(), e.getMessage()));
    }

    /** 도메인 규칙 위반 — 상태 전이 불가 등 IllegalStateException 을 400 으로 처리 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException e) {
        log.error("Domain rule violation: {}", e.getMessage());

        return ResponseEntity.status(ErrorCode.INVALID_ORDER_STATUS.getStatus())
                .body(ApiResponse.failure(ErrorCode.INVALID_ORDER_STATUS.getCode(), e.getMessage()));
    }

    /** 요청 바디 바인딩 실패 (@Valid 검증) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation exception: {}", e.getMessage());

        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getDefaultMessage())
                .orElse(ErrorCode.INVALID_INPUT.getMessage());

        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus())
                .body(ApiResponse.failure(ErrorCode.INVALID_INPUT.getCode(), message));
    }

    /** JSON 파싱 실패 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Message not readable: {}", e.getMessage());

        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus())
                .body(ApiResponse.failure(ErrorCode.INVALID_INPUT.getCode(), ErrorCode.INVALID_INPUT.getMessage()));
    }

    /** 그 외 모든 예외 — 500 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception", e);

        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.failure(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
