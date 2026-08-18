package com.example.bai5;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {
    private final IncidentETLService incidentETLService;

    @GetMapping
    public IncidentReport createIncident(@RequestParam String rawText) {
        return incidentETLService.process(rawText);
    }
}
