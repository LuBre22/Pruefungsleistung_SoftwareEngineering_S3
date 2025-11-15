package com.sitzplatzverwaltung.domain.model;

/**
 * Status des UpgradeSagaOrchestrators.
 * Entspricht dem UML-Klassendiagramm v4.
 */
public enum OrchestratorState {
    IDLE,
    RESERVING,
    PAYING,
    COMMITTING,
    ROLLBACK_PENDING
}
