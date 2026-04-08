package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.LineStatusDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.LineMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LineQueryServiceTest {

    @Mock
    private LineMapper lineMapper;

    @InjectMocks
    private LineQueryService lineQueryService;

    @Test
    @DisplayName("라인 요약 조회 시 Mapper가 1회 호출된다")
    void getLinesSummary_CallsMapperOnce() {
        // given
        given(lineMapper.findLinesSummary()).willReturn(List.of());

        // when
        lineQueryService.getLinesSummary();

        // then
        verify(lineMapper, times(1)).findLinesSummary();
    }

    @Test
    @DisplayName("라인 상태 조회 시 Mapper가 1회 호출된다")
    void getLineStatus_CallsMapperOnce() {
        // given
        given(lineMapper.findLineStatus(anyLong())).willReturn(new LineStatusDto());

        // when
        lineQueryService.getLineStatus(1L);

        // then
        verify(lineMapper, times(1)).findLineStatus(anyLong());
    }
}
