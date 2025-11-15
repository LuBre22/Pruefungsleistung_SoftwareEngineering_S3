package com.sitzplatzverwaltung.domain.model;

import lombok.*;

/**
 * Mitarbeiter-Entität für RBAC.
 * Invariante: mitarbeiter_id NOT NULL, role NOT NULL
 * Traceability: Zugriffskontrolle, Supervisor-Prüfung
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mitarbeiter {
    private String mitarbeiter_id;
    private String name;
    private EmployeeRole role;
}
