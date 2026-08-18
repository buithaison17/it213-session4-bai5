package com.example.bai5;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConsoleAlertService {

    public void sendAlert(IncidentReport incident) {

        // Giả lập lỗi
        if ("ERROR".equalsIgnoreCase(incident.getDescription())) {
            throw new RuntimeException("Console Alert bị lỗi!");
        }

        log.error("""
                        
                        🚨🚨🚨 RED ALERT 🚨🚨🚨
                        Order     : {}
                        Vehicle   : {}
                        Urgency   : {}
                        Time      : {}
                        Description: {}
                        🚨🚨🚨🚨🚨🚨🚨🚨🚨
                        """,
                incident.getOrderCode(),
                incident.getLicensePlate(),
                incident.getUrgency(),
                incident.getIncidentTime(),
                incident.getDescription()
        );
    }
}


