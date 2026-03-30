package com.ohgiraffers.team3backendscm.common;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 모든 REST API 응답에서 사용하는 공통 응답 래퍼 클래스.
 * 성공/실패 여부, 실제 데이터, 에러 코드·메시지, 응답 생성 시각을 일관된 형식으로 제공하여
 * 클라이언트가 응답을 동일한 방식으로 처리할 수 있도록 한다.
 *
 * @param <T> 응답에 포함될 데이터 타입
 */
@Getter
@Builder
public class ApiResponse<T> {

	private Boolean success;         // 요청 성공 여부
	private T data;                  // 실제 데이터 (성공 시 사용)
	private String errorCode;        // 에러 코드 (실패 시 사용)
	private String message;          // 에러 메시지
	private LocalDateTime timestamp; // 응답 생성 시간

	/**
	 * 성공 응답을 생성하는 정적 팩토리 메서드.
	 * success=true, 현재 시각이 자동 설정된다.
	 *
	 * @param data 응답에 포함할 데이터 (없으면 null)
	 * @param <T>  데이터 타입
	 * @return 성공 ApiResponse 객체
	 */
	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder()
			.success(true)
			.data(data)
			.timestamp(LocalDateTime.now())
			.build();
	}

	/**
	 * 실패 응답을 생성하는 정적 팩토리 메서드.
	 * success=false, 현재 시각이 자동 설정된다.
	 *
	 * @param errorCode 에러 코드 문자열 (예: "ORDER_NOT_FOUND")
	 * @param message   사용자에게 노출할 에러 메시지
	 * @param <T>       데이터 타입 (실패 시 data 는 null)
	 * @return 실패 ApiResponse 객체
	 */
	public static <T> ApiResponse<T> failure(String errorCode, String message) {
		return ApiResponse.<T>builder()
			.success(false)
			.errorCode(errorCode)
			.message(message)
			.timestamp(LocalDateTime.now())
			.build();
	}

}
