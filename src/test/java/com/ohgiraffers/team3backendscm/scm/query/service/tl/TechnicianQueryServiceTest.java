package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.scm.query.mapper.TechnicianMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TechnicianQueryServiceTest {

    @Mock
    private TechnicianMapper technicianMapper;

    @InjectMocks
    private TechnicianQueryService technicianQueryService;

    @Test
    @DisplayName("기술자 목록 조회 시 Mapper가 1회 호출된다")
    void getTechnicians_CallsMapperOnce() {
        // given
        given(technicianMapper.findTechnicians()).willReturn(List.of());

        // when
        technicianQueryService.getTechnicians();

        // then
        verify(technicianMapper, times(1)).findTechnicians();
    }
}
