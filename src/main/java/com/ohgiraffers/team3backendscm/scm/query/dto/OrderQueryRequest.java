package com.ohgiraffers.team3backendscm.scm.query.dto;

import com.ohgiraffers.team3backendscm.scm.command.domain.aggregate.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderQueryRequest {

    private OrderStatus status;
    private String keyword;
    private Integer page;
    private Integer size;
}
