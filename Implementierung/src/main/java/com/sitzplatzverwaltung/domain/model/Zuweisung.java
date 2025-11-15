package com.sitzplatzverwaltung.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Zuweisung eines Sitzplatzes an einen Passagier.
 * Invariante: mitarbeiter_id NOT NULL AND zeitstempel NOT NULL
 * Audit-Attribute (Compliance): mitarbeiter_id (wer), zeitstempel (wann), grund (warum)
 * correlation_id dient der Idempotenz; bei erfolgreicher Zuweisung gesetzt, bei Fehler null.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Zuweisung {
    private UUID assignment_id;
    private String passenger_id;
    private String seat_number;
    private String mitarbeiter_id;
    private LocalDateTime zeitstempel;
    private AssignmentReason grund;
    private UUID correlation_id; // optional, für Idempotenz
}
