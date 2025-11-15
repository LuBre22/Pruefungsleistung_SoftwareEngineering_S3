package com.sitzplatzverwaltung.domain.model;

/**
 * Status einer Saga-Transaktion (Upgrade-Flow).
 * Entspricht dem UML-Klassendiagramm v4.
 */
public enum SagaStatus {
    STARTED,
    COMPENSATED,
    COMPLETED
}
