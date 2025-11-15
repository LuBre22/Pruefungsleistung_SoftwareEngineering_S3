package com.sitzplatzverwaltung.domain.model;

/**
 * Status einer Zahlung im Upgrade-Flow.
 * Entspricht dem UML-Klassendiagramm v4.
 */
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED
}
