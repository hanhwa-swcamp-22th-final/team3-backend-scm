package com.ohgiraffers.team3backendscm.common.idgenerator;

/**
 * 고유 ID를 생성하는 전략 인터페이스.
 * Command 레이어에서 엔티티 PK를 DB auto_increment 대신 애플리케이션 레벨에서 직접 할당할 때 사용한다.
 * 실 운영에서는 TimeBasedIdGenerator, 테스트에서는 Mock 구현체로 교체할 수 있다.
 */
public interface IdGenerator {

    /**
     * 유일한 Long 타입 ID를 생성하여 반환한다.
     *
     * @return 생성된 고유 ID
     */
    Long generate();
}
