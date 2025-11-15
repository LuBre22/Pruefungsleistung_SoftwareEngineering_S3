package com.sitzplatzverwaltung.domain.model;

/**
 * Status einer Sitzplatzreservierung (Upgrade-Flow).
 * Entspricht dem UML-Klassendiagramm v4.
 */
public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    EXPIRED,
    COMPLETED
}
