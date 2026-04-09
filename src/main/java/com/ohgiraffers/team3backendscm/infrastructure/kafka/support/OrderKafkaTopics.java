package com.ohgiraffers.team3backendscm.infrastructure.kafka.support;

public final class OrderKafkaTopics {

    public static final String ORDER_REGISTERED = "scm.order.registered";
    public static final String ORDER_DIFFICULTY_ANALYZED = "batch.order-difficulty.analyzed";
    public static final String ORDER_DIFFICULTY_SNAPSHOT = "scm.order-difficulty.snapshot";
    public static final String ASSIGNMENT_SNAPSHOT = "scm.assignment.snapshot";

    private OrderKafkaTopics() {
    }
}
