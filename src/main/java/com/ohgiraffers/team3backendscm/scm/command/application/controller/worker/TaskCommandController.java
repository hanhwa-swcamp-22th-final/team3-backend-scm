package com.ohgiraffers.team3backendscm.scm.command.application.controller.worker;

import com.ohgiraffers.team3backendscm.common.dto.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.TaskFinishRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.worker.TaskCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ?묒뾽??Worker) 沅뚰븳???묒뾽 ?ㅽ뻾 Command REST 而⑦듃濡ㅻ윭.
 * 湲곕낯 寃쎈줈: /api/v1/scm
 * <p>
 * ?쒓났 ?붾뱶?ъ씤??
 * <ul>
 *   <li>POST /workers/me/today-tasks/{taskId}/start         - ?묒뾽 ?쒖옉</li>
 *   <li>POST /workers/me/today-tasks/{taskId}/finish-draft  - ?묒뾽 醫낅즺 ?꾩떆???/li>
 *   <li>POST /workers/me/today-tasks/{taskId}/finish        - ?묒뾽 醫낅즺 ?쒖텧</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/scm")
@RequiredArgsConstructor
public class TaskCommandController {

    private final TaskCommandService taskCommandService;

    /**
     * ?묒뾽???쒖옉?쒕떎.
     * MatchingRecord ??workStartAt ???꾩옱 ?쒓컖?쇰줈 湲곕줉?쒕떎.
     *
     * @param taskId ?쒖옉???묒뾽??諛곗젙 湲곕줉 ID (matching_record_id)
     * @return ?깃났 ?щ?瑜??댁? ApiResponse (data = null)
     */
    @PostMapping("/workers/me/today-tasks/{taskId}/start")
    public ResponseEntity<ApiResponse<Void>> startTask(@PathVariable Long taskId) {
        taskCommandService.startTask(taskId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * ?묒뾽 寃곌낵瑜??꾩떆??ν븳??
     * workEndAt쨌comment 瑜?湲곕줉?섎릺 諛곗젙 ?곹깭???좎??쒕떎.
     *
     * @param taskId  ?꾩떆??ν븷 ?묒뾽??諛곗젙 湲곕줉 ID
     * @param request 肄붾찘?몃? ?댁? ?붿껌 DTO
     * @return ?깃났 ?щ?瑜??댁? ApiResponse (data = null)
     */
    @PostMapping("/workers/me/today-tasks/{taskId}/finish-draft")
    public ResponseEntity<ApiResponse<Void>> finishDraft(
            @PathVariable Long taskId,
            @RequestBody TaskFinishRequest request) {
        taskCommandService.finishDraft(taskId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * ?묒뾽???꾨즺 ?쒖텧?쒕떎.
     * MatchingRecord ?곹깭瑜?COMPLETE 濡??꾪솚?섍퀬 二쇰Ц ?곹깭瑜?COMPLETED 濡?蹂寃쏀븳??
     *
     * @param taskId  ?꾨즺???묒뾽??諛곗젙 湲곕줉 ID
     * @param request 肄붾찘?몃? ?댁? ?붿껌 DTO
     * @return ?깃났 ?щ?瑜??댁? ApiResponse (data = null)
     */
    @PostMapping("/workers/me/today-tasks/{taskId}/finish")
    public ResponseEntity<ApiResponse<Void>> finish(
            @PathVariable Long taskId,
            @RequestBody TaskFinishRequest request) {
        taskCommandService.finish(taskId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
