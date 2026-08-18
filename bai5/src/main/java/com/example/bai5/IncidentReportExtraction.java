package com.example.bai5;


import java.time.LocalDateTime;

public record IncidentReportExtraction(
        String orderCode,
        String licensePlate,
        String urgency,
        String description,
        LocalDateTime incidentTime
) {
}

