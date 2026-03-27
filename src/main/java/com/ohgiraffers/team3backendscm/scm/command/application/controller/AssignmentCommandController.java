package com.ohgiraffers.team3backendscm.scm.command.application.controller;

import com.ohgiraffers.team3backendscm.common.ApiResponse;
import com.ohgiraffers.team3backendscm.scm.command.application.dto.request.AssignRequest;
import com.ohgiraffers.team3backendscm.scm.command.application.service.AssignmentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scm/task-matching")
@RequiredArgsConstructor
public class AssignmentCommandController {

    private final AssignmentCommandService assignmentCommandService;

    @PostMapping("/assignments")
    public ResponseEntity<ApiResponse<Void>> createAssignment(@RequestBody AssignRequest request) {
        assignmentCommandService.assign(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
