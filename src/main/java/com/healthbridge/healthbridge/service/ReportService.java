package com.healthbridge.healthbridge.service;

import com.healthbridge.healthbridge.dto.response.ReportResponse;
import com.healthbridge.healthbridge.model.FamilyMember;
import com.healthbridge.healthbridge.model.HealthReport;
import com.healthbridge.healthbridge.model.User;
import com.healthbridge.healthbridge.repository.FamilyMemberRepository;
import com.healthbridge.healthbridge.repository.ReportRepository;
import com.healthbridge.healthbridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;
    private final PdfParsingService pdfParsingService;
    private final LlmService llmService;

    public ReportResponse uploadReport(String email,
                                       Long memberId,
                                       String labName,
                                       String reportType,
                                       MultipartFile file) {

        // Verify user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify family member belongs to this user
        FamilyMember member = familyMemberRepository
                .findByIdAndUserId(memberId, user.getId())
                .orElseThrow(() -> new RuntimeException("Family member not found"));

        // Extract text from PDF
        String rawText = pdfParsingService.extractText(file);
        System.out.println(">>> PDF TEXT EXTRACTED SUCCESSFULLY");

        // Call LLM to explain the report
        System.out.println(">>> CALLING LLM FOR EXPLANATION...");
        String aiExplanation = llmService.explainReport(rawText, user.getPreferredLanguage());
        System.out.println(">>> AI EXPLANATION RECEIVED");

        // Call LLM for Indian diet recommendations
        System.out.println(">>> CALLING LLM FOR DIET RECOMMENDATIONS...");
        String dietRecommendations = llmService.getDietRecommendations(rawText, user.getPreferredLanguage());
        System.out.println(">>> DIET RECOMMENDATIONS RECEIVED");

        // Save file path
        String fileUrl = "uploads/" + file.getOriginalFilename();

        // Save report to DB
        HealthReport report = HealthReport.builder()
                .labName(labName)
                .reportType(reportType)
                .fileUrl(fileUrl)
                .rawText(rawText)
                .aiExplanation(aiExplanation)
                .dietRecommendations(dietRecommendations)
                .familyMember(member)
                .build();

        HealthReport saved = reportRepository.save(report);
        return mapToResponse(saved);
    }

    public List<ReportResponse> getReports(String email, Long memberId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        familyMemberRepository.findByIdAndUserId(memberId, user.getId())
                .orElseThrow(() -> new RuntimeException("Family member not found"));

        return reportRepository.findByFamilyMemberIdOrderByUploadedAtDesc(memberId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReportResponse getReport(String email, Long memberId, Long reportId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        familyMemberRepository.findByIdAndUserId(memberId, user.getId())
                .orElseThrow(() -> new RuntimeException("Family member not found"));

        HealthReport report = reportRepository
                .findByIdAndFamilyMemberId(reportId, memberId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        return mapToResponse(report);
    }

    private ReportResponse mapToResponse(HealthReport report) {
        return ReportResponse.builder()
                .id(report.getId())
                .labName(report.getLabName())
                .reportType(report.getReportType())
                .fileUrl(report.getFileUrl())
                .rawText(report.getRawText())
                .aiExplanation(report.getAiExplanation())
                .dietRecommendations(report.getDietRecommendations())
                .uploadedAt(report.getUploadedAt())
                .familyMemberId(report.getFamilyMember().getId())
                .familyMemberName(report.getFamilyMember().getFullName())
                .build();
    }
}