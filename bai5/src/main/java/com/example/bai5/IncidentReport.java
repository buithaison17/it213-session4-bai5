package com.example.bai5;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class IncidentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderCode;
    private String licensePlate;
    private String urgency;
    private String description;
    private LocalDateTime incidentTime;

    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;
}

