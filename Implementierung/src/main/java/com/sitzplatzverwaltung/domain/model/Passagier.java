package com.sitzplatzverwaltung.domain.model;

import lombok.*;

/**
 * Passagier-Entität (aus Buchungssystem).
 * Invariante: passenger_id NOT NULL, name NOT NULL
 * Traceability: Sitzplatzzuweisung, Buchungsabgleich
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Passagier {
    private String passenger_id;
    private String name;
}
