package com.example.bai5;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentETLService {

    private final ChatModel chatModel;
    private final IncidentReportRepository repository;
    private final ConsoleAlertService alertService;

    public IncidentReport process(String rawText) {

        // AI output -> IncidentReportExtraction
        BeanOutputConverter<IncidentReportExtraction> converter =
                new BeanOutputConverter<>(IncidentReportExtraction.class);

        String prompt = """
                Bạn là AI phân tích sự cố logistics.
                
                Hãy trích xuất thông tin từ nội dung:
                - orderCode
                - licensePlate
                - urgency: LOW, MEDIUM, HIGH hoặc CRITICAL
                - description
                - incidentTime
                
                 QUY TẮC QUAN TRỌNG:
                        - incidentTime phải có format ISO-8601: yyyy-MM-dd'T'HH:mm:ss
                        - Ví dụ: 2026-08-17T15:00:00
                        - Không được trả về dạng "15:00 ngày 17/08/2026"
                        - Chỉ trả về JSON.
                
                %s
                
                %s
                """.formatted(
                rawText,
                converter.getFormat()
        );

        // Raw text -> AI
        String response = chatModel.call(prompt);

        // AI response -> Object
        IncidentReportExtraction extraction =
                converter.convert(response);

        // Mapping -> Entity
        IncidentReport incident = new IncidentReport();

        incident.setOrderCode(extraction.orderCode());
        incident.setLicensePlate(extraction.licensePlate());
        incident.setUrgency(extraction.urgency());
        incident.setDescription(extraction.description());
        incident.setIncidentTime(extraction.incidentTime());

        // Phase 1: Save DB
        incident.setNotificationStatus(
                NotificationStatus.PENDING
        );

        IncidentReport saved = repository.save(incident);

        // Check urgency
        boolean urgent =
                "HIGH".equalsIgnoreCase(saved.getUrgency())
                        || "CRITICAL".equalsIgnoreCase(saved.getUrgency());

        if (!urgent) {

            saved.setNotificationStatus(
                    NotificationStatus.NOT_REQUIRED
            );

            return repository.save(saved);
        }

        // Phase 2: Console Alert
        try {

            alertService.sendAlert(saved);

            saved.setNotificationStatus(
                    NotificationStatus.SUCCESS
            );

        } catch (Exception e) {

            log.error(
                    "Console Alert failed, incidentId={}",
                    saved.getId(),
                    e
            );

            saved.setNotificationStatus(
                    NotificationStatus.FAILED
            );
        }

        return repository.save(saved);
    }
}
