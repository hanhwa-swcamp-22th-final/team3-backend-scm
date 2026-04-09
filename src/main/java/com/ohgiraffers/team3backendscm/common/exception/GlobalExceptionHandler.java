package com.ohgiraffers.team3backendscm.common.exception;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
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

    /* 비즈니스 예외 처리: ErrorCode 기반 상태 코드와 메시지 반환 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("Business exception: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), e.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    /* NoSuchElementException 예외 처리: 404 Not Found 처리 */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoSuchElementException(NoSuchElementException e) {
        log.error("Resource not found: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(
            ErrorCode.ORDER_NOT_FOUND.getCode(),
            e.getMessage()
        );
        return ResponseEntity.status(ErrorCode.ORDER_NOT_FOUND.getStatus()).body(response);
    }

    /* IllegalStateException 예외 처리: 400 Bad Request 처리 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException e) {
        log.error("Domain rule violation: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(
            ErrorCode.INVALID_ORDER_STATUS.getCode(),
            e.getMessage()
        );
        return ResponseEntity.status(ErrorCode.INVALID_ORDER_STATUS.getStatus()).body(response);
    }

    /* 요청 바디 바인딩 실패 (@Valid 검증 오류) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation exception: {}", e.getMessage());

        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getDefaultMessage())
                .orElse(ErrorCode.INVALID_INPUT.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(ErrorCode.INVALID_INPUT.getCode(), message);
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus()).body(response);
    }

    /* JSON 파싱 실패 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("Message not readable: {}", e.getMessage());

        ApiResponse<Void> response = ApiResponse.failure(
            ErrorCode.INVALID_INPUT.getCode(),
            ErrorCode.INVALID_INPUT.getMessage()
        );
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus()).body(response);
    }

    /* 기타 모든 예외 처리: 500 Internal Server Error */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception", e);

        ApiResponse<Void> response = ApiResponse.failure(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(response);
    }
}
