package com.ohgiraffers.team3backendscm.scm.query.service.tl;

import com.ohgiraffers.team3backendscm.infrastructure.client.AdminClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.HrClient;
import com.ohgiraffers.team3backendscm.infrastructure.client.dto.AdminEmployeeProfileResponse;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.DifficultyGrade;
import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.MatchingMode;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentCandidateDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentDetailDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentRebalanceDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentRebalanceWorkerRow;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.AssignmentTimelineDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.OrderOcsaDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.AssignmentMapper;
import com.ohgiraffers.team3backendscm.scm.query.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 팀 리더(TL) 권한의 배정 조회 Query 서비스.
 * AssignmentMapper를 통해 후보 기술자, 배정 요약, 타임라인, 재조정 현황 등을 읽기 전용으로 제공한다.
 */
@Service
@RequiredArgsConstructor
public class AssignmentQueryService {

    private final AssignmentMapper assignmentMapper;
    private final OrderMapper orderMapper;
    private final AdminClient adminClient;
    private final HrClient hrClient;

    /**
     * 배정 기록 ID로 배정 상세 정보를 조회한다.
     *
     * @param matchingRecordId 조회할 배정 기록 PK
     * @return 배정 상세 DTO
     * @throws NoSuchElementException 배정 기록이 존재하지 않을 경우
     */
    public AssignmentDetailDto getAssignment(Long matchingRecordId) {
        AssignmentDetailDto detail = assignmentMapper.findById(matchingRecordId)
                .orElseThrow(() -> new NoSuchElementException("배정 기록을 찾을 수 없습니다. id=" + matchingRecordId));
        AdminEmployeeProfileResponse profile = getProfile(detail.getEmployeeId());
        return new AssignmentDetailDto(
                detail.getMatchingRecordId(),
                detail.getOrderId(),
                detail.getOrderNo(),
                detail.getOrderStatus(),
                detail.getEmployeeId(),
                profile == null ? null : profile.getEmployeeName(),
                profile == null ? null : profile.getCurrentTier(),
                detail.getMatchingMode(),
                detail.getMatchingStatus(),
                detail.getDcRatio(),
                detail.getExpectedBonus(),
                detail.getExpectedProductivity(),
                detail.getQualityRisk(),
                detail.getWorkStartAt(),
                detail.getWorkEndAt(),
                detail.getComment(),
                detail.getAssignedAt()
        );
    }

    /**
     * 배정 가능한 기술자 정보 목록을 조회한다.
     *
     * @return 후보 기술자 목록 (보유 티어, OCSA 점수, 적합도 포함)
     */
    public List<AssignmentCandidateDto> getCandidates() {
        return getCandidates(null);
    }

    public List<AssignmentCandidateDto> getCandidates(Long orderId) {
        List<Long> teamMemberIds = hrClient.getTeamMembers().stream()
                .map(member -> member.getEmployeeId())
                .toList();
        if (teamMemberIds.isEmpty()) {
            return List.of();
        }

        DifficultyGrade difficultyGrade = getDifficultyGrade(orderId);
        return assignmentMapper.findCandidatesByEmployeeIds(teamMemberIds, orderId).stream()
                .map(candidate -> new AssignmentCandidateDto(
                        candidate.getEmployeeId(),
                        candidate.getEmployeeName(),
                        candidate.getTier(),
                        candidate.getScore(),
                        calculateSuitability(candidate.getScore(), candidate.getTier(), difficultyGrade),
                        MatchingMode.determine(difficultyGrade, candidate.getTier())
                ))
                .sorted((left, right) -> nullSafe(right.getSuitabilityScore()).compareTo(nullSafe(left.getSuitabilityScore())))
                .toList();
    }

    /**
     * 오늘 배정 수, 미배정 주문 수, 배정 정확도 등 배정 현황 요약을 조회한다.
     *
     * @return 배정 현황 요약 DTO
     */
    public AssignmentSummaryDto getSummary() {
        return assignmentMapper.findSummary();
    }

    /**
     * 라인별 배정 타임라인(기술자, 날짜, 주문 상태)을 조회한다.
     *
     * @return 배정 타임라인 목록
     */
    public List<AssignmentTimelineDto> getTimeline() {
        Map<Long, AdminEmployeeProfileResponse> profileCache = new LinkedHashMap<>();
        return assignmentMapper.findTimeline().stream()
                .map(timeline -> enrichTimeline(timeline, profileCache))
                .toList();
    }

    /**
     * 라인별 기술자 티어 분포 및 권장 배치 인원 포함 재조정 현황을 조회한다.
     *
     * @return 라인별 재조정 현황 목록
     */
    public List<AssignmentRebalanceDto> getRebalance() {
        List<AssignmentRebalanceWorkerRow> rows = assignmentMapper.findRebalanceWorkers();
        Map<Long, List<AssignmentRebalanceWorkerRow>> rowsByLine = rows.stream()
                .collect(Collectors.groupingBy(
                        AssignmentRebalanceWorkerRow::getFactoryLineId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return rowsByLine.values().stream()
                .map(this::toRebalanceDto)
                .toList();
    }

    private AssignmentRebalanceDto toRebalanceDto(List<AssignmentRebalanceWorkerRow> rows) {
        AssignmentRebalanceWorkerRow first = rows.get(0);
        Map<String, Long> tierCounts = rows.stream()
                .map(AssignmentRebalanceWorkerRow::getEmployeeId)
                .filter(Objects::nonNull)
                .distinct()
                .map(this::getProfile)
                .filter(Objects::nonNull)
                .map(AdminEmployeeProfileResponse::getCurrentTier)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        int total = tierCounts.values().stream().mapToInt(Long::intValue).sum();
        int tierS = tierCounts.getOrDefault("S", 0L).intValue();
        int tierA = tierCounts.getOrDefault("A", 0L).intValue();
        int tierB = tierCounts.getOrDefault("B", 0L).intValue();
        int tierC = tierCounts.getOrDefault("C", 0L).intValue();

        return new AssignmentRebalanceDto(
                first.getFactoryLineId(),
                first.getFactoryLineName(),
                total,
                tierS,
                tierA,
                tierB,
                tierC,
                tierB + tierC
        );
    }

    private AssignmentTimelineDto enrichTimeline(
            AssignmentTimelineDto timeline,
            Map<Long, AdminEmployeeProfileResponse> profileCache
    ) {
        AdminEmployeeProfileResponse profile = profileCache.computeIfAbsent(
                timeline.getEmployeeId(),
                this::getProfile
        );

        return new AssignmentTimelineDto(
                timeline.getFactoryLineId(),
                timeline.getFactoryLineName(),
                timeline.getEmployeeId(),
                profile == null ? timeline.getEmployeeName() : profile.getEmployeeName(),
                profile == null ? timeline.getEmployeeTier() : profile.getCurrentTier(),
                timeline.getAssignedDate(),
                timeline.getMatchingStatus(),
                timeline.getOrderNo(),
                timeline.getOrderStatus(),
                timeline.getWorkStartAt(),
                timeline.getWorkEndAt()
        );
    }

    private AdminEmployeeProfileResponse getProfile(Long employeeId) {
        if (employeeId == null) {
            return null;
        }
        return adminClient.getEmployeeProfile(employeeId);
    }

    private DifficultyGrade getDifficultyGrade(Long orderId) {
        if (orderId == null) {
            return null;
        }
        OrderOcsaDto ocsa = orderMapper.findOrderOcsa(orderId);
        return ocsa == null ? null : ocsa.getDifficultyGrade();
    }

    private BigDecimal calculateSuitability(BigDecimal ocsaScore, String tier, DifficultyGrade difficultyGrade) {
        if (ocsaScore == null) {
            return null;
        }
        BigDecimal scoreRatio = ocsaScore.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal tierFit = BigDecimal.valueOf(calculateTierFit(tier, difficultyGrade));
        return scoreRatio.multiply(BigDecimal.valueOf(0.7))
                .add(tierFit.multiply(BigDecimal.valueOf(0.3)))
                .min(BigDecimal.ONE)
                .setScale(4, RoundingMode.HALF_UP);
    }

    private double calculateTierFit(String tier, DifficultyGrade difficultyGrade) {
        if (difficultyGrade == null) {
            return 1.0;
        }
        int gap = Math.abs(tierRank(tier) - difficultyGrade.getRequiredTierRank());
        return Math.max(0.0, 1.0 - gap * 0.25);
    }

    private int tierRank(String tier) {
        if (tier == null) {
            return 0;
        }
        return switch (tier) {
            case "S" -> 3;
            case "A" -> 2;
            case "B" -> 1;
            default -> 0;
        };
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.valueOf(-1) : value;
    }
}
