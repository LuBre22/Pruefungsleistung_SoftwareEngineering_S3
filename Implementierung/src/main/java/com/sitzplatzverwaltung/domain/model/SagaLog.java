package com.sitzplatzverwaltung.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Audit-Log für Saga-Transaktionen (Upgrade-Flow).
 * Invariante: saga_id NOT NULL, step NOT NULL, status NOT NULL, timestamp NOT NULL
 * Traceability: Saga-Pattern, Kompensation, Audit
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SagaLog {
    private UUID saga_id;
    private String step;
    private SagaStatus status;
    private LocalDateTime timestamp;
}
