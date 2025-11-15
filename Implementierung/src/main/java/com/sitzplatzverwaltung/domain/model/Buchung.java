package com.sitzplatzverwaltung.domain.model;

import lombok.*;

/**
 * Buchung-Entität (aus Buchungssystem).
 * Invariante: booking_reference NOT NULL, passenger_id NOT NULL, cabin_class NOT NULL
 * Traceability: Buchungsabgleich, Sitzplatzzuweisung
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Buchung {
    private String booking_reference;
    private String passenger_id;
    private CabinClass cabin_class;
}
