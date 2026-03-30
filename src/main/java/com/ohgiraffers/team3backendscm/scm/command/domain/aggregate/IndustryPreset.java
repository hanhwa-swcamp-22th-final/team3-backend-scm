package com.ohgiraffers.team3backendscm.scm.command.domain.aggregate;

/**
 * OCSA 가중치 설정에서 사용하는 산업군 프리셋 열거형.
 * 산업군마다 공정 복잡도·품질 정밀도 등의 중요도가 다르기 때문에,
 * 각 프리셋에 맞는 가중치 설정(OcsaWeightConfig)을 선택하여 난이도를 산출한다.
 */
public enum IndustryPreset {
    SEMICONDUCTOR, // 반도체 산업
    DISPLAY,       // 디스플레이 산업
    BATTERY,       // 배터리(2차전지) 산업
    VEHICLE,       // 자동차 산업
    CUSTOM         // 사용자 정의 (위 프리셋에 해당하지 않는 경우)
}
