package com.sitzplatzverwaltung.domain.model;

import lombok.*;
import java.util.UUID;

/**
 * Sitzplatz-Entität im Airbus A350-900.
 * Invariante: seat_number NOT NULL, cabin_class NOT NULL, status NOT NULL
 * Traceability: Sitzplan, Statusverwaltung
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sitzplatz {
    private String seat_number; // z.B. "12A"
    private CabinClass cabin_class;
    private int row;
    private String column;
    private SeatStatus status;
    private UUID reservation_id; // optional, für temporäre Reservierungen (Upgrade)
}
