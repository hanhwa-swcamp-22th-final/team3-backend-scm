package com.ohgiraffers.team3backendscm.common.idgenerator;

/**
 * Strategy interface for generating unique IDs.
 */
public interface IdGenerator {

    Long generate();
}