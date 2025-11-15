package com.sitzplatzverwaltung.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reservierung eines Sitzplatzes (Upgrade-Flow).
 * Invariante: reservation_id NOT NULL, seat_number NOT NULL, status NOT NULL
 * Traceability: Upgrade-Transaktionen, Saga-Pattern
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reservierung {
    private UUID reservation_id;
    private String seat_number;
    private ReservationStatus status;
    private LocalDateTime ttl_expires_at;
}
