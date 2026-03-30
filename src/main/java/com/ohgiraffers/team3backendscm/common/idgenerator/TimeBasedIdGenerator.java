package com.ohgiraffers.team3backendscm.common.idgenerator;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 현재 시각(밀리초)과 무작위 접미사를 조합하여 고유 Long ID를 생성하는 구현체.
 * IdGenerator 인터페이스의 기본 운영 구현체로, Spring Bean 으로 등록된다.
 * <p>
 * ID 구조: (currentTimeMillis × 1000) + 랜덤(100~999)
 * AtomicLong 을 사용해 동일 밀리초 내 중복 ID가 발생하지 않도록 단조 증가(monotonic increment)를 보장한다.
 * </p>
 */
@Component
public class TimeBasedIdGenerator implements IdGenerator {

    private static final long RANDOM_BOUND = 1_000L; // 랜덤 접미사 상한 (exclusive)
    private static final long RANDOM_MIN   = 100L;   // 랜덤 접미사 하한 (inclusive)

    /** 마지막으로 생성한 ID를 기억하여 단조 증가를 보장하는 원자적 변수 */
    private final AtomicLong lastGeneratedId = new AtomicLong(0L);

    /**
     * 고유 ID를 생성한다.
     * 후보 값이 이전에 생성된 ID 이하라면 이전 ID + 1 을 반환하여 중복을 방지한다.
     *
     * @return 단조 증가하는 고유 Long ID
     */
    @Override
    public Long generate() {
        long candidate = createCandidate();

        return lastGeneratedId.updateAndGet(previousId ->
            Math.max(candidate, previousId + 1)
        );
    }

    /**
     * 현재 시각(밀리초)과 3자리 난수를 조합하여 후보 ID를 만든다.
     *
     * @return 후보 ID 값
     */
    private long createCandidate() {
        long millis = System.currentTimeMillis();
        long randomSuffix = ThreadLocalRandom.current().nextLong(RANDOM_MIN, RANDOM_BOUND);

        return (millis * 1000) + randomSuffix;
    }
}
