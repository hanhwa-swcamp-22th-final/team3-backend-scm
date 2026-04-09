package com.ohgiraffers.team3backendscm.scm.query.service.worker;

import com.ohgiraffers.team3backendscm.scm.query.dto.response.TaskDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerDeploymentDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerMatchingHistoryDto;
import com.ohgiraffers.team3backendscm.scm.query.dto.response.WorkerTaskSummaryDto;
import com.ohgiraffers.team3backendscm.scm.query.mapper.WorkerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ?묒뾽??Worker) 沅뚰븳??蹂몄씤 ?대젰 議고쉶 Query ?쒕퉬??
 * WorkerMapper 瑜??듯빐 ?묒뾽???먯떊???ㅻ퉬 諛곗튂 ?대젰怨?二쇰Ц 諛곗젙 ?대젰???쎄린 ?꾩슜?쇰줈 ?쒓났?쒕떎.
 */
@Service
@RequiredArgsConstructor
public class WorkerQueryService {

    private final WorkerMapper workerMapper;

    /**
     * ?뱀젙 ?묒뾽?먯쓽 誘몄셿猷??묒뾽 紐⑸줉??議고쉶?쒕떎.
     * ?좎쭨 臾닿??섍쾶 REJECT쨌COMPLETE ?곹깭瑜??쒖쇅???꾩껜 諛곗젙 ?묒뾽??諛섑솚?쒕떎.
     *
     * @param employeeId 議고쉶???묒뾽??吏곸썝) ID
     * @return 誘몄셿猷??묒뾽 紐⑸줉
     */
    public List<TaskDto> getMyPendingTasks(Long employeeId) {
        return workerMapper.findMyPendingTasks(employeeId);
    }

    /**
     * ?뱀젙 ?묒뾽?먯쓽 ?ㅻ퉬 諛곗튂 ?대젰??議고쉶?쒕떎.
     *
     * @param employeeId 議고쉶???묒뾽??吏곸썝) ID
     * @return ?ㅻ퉬 諛곗튂 ?대젰 紐⑸줉
     */
    public List<WorkerDeploymentDto> getMyDeployments(Long employeeId) {
        return workerMapper.findMyDeployments(employeeId);
    }

    /**
     * ?뱀젙 ?묒뾽?먯쓽 二쇰Ц 諛곗젙 ?대젰??議고쉶?쒕떎.
     *
     * @param employeeId 議고쉶???묒뾽??吏곸썝) ID
     * @return 二쇰Ц 諛곗젙 ?대젰 紐⑸줉
     */
    public List<WorkerMatchingHistoryDto> getMyMatchingHistory(Long employeeId) {
        return workerMapper.findMyMatchingHistory(employeeId);
    }

    /**
     * ?뱀젙 ?묒뾽?먯쓽 ?곹깭蹂??묒뾽 ?섎? 吏묎퀎?쒕떎.
     * CONFIRM쨌INPROGRESS쨌COMPLETE ?곹깭蹂?移댁슫?몃? 諛섑솚?쒕떎.
     *
     * @param employeeId 議고쉶???묒뾽??吏곸썝) ID
     * @return ?곹깭蹂??묒뾽 ??吏묎퀎 DTO
     */
    public WorkerTaskSummaryDto getMyTaskSummary(Long employeeId) {
        return workerMapper.findMyTaskSummary(employeeId);
    }
}
