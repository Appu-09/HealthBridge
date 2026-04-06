package com.healthbridge.healthbridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    private Long id;
    private String labName;
    private String reportType;
    private String fileUrl;
    private String rawText;
    private String aiExplanation;
    private String dietRecommendations;
    private LocalDateTime uploadedAt;
    private Long familyMemberId;
    private String familyMemberName;
}