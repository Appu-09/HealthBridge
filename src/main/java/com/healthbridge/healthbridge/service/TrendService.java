package com.healthbridge.healthbridge.service;

import com.healthbridge.healthbridge.model.HealthReport;
import com.healthbridge.healthbridge.model.User;
import com.healthbridge.healthbridge.repository.FamilyMemberRepository;
import com.healthbridge.healthbridge.repository.ReportRepository;
import com.healthbridge.healthbridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrendService {

    private final ReportRepository reportRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;
    private final AlertService alertService;
    private final LlmService llmService;

    // Runs every Monday at 9:00 AM
    @Scheduled(cron = "0 0 9 * * MON")
    public void runWeeklyTrendCheck() {
        System.out.println(">>> RUNNING WEEKLY TREND CHECK...");
        userRepository.findAll().forEach(this::checkTrendsForUser);
    }

    public String analyzeTrendsForMember(String email, Long memberId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        familyMemberRepository.findByIdAndUserId(memberId, user.getId())
                .orElseThrow(() -> new RuntimeException("Family member not found"));

        List<HealthReport> reports = reportRepository
                .findByFamilyMemberIdOrderByUploadedAtDesc(memberId);

        if (reports.size() < 2) {
            return "At least 2 reports are needed to analyze trends. " +
                    "Please upload more reports over time.";
        }

        // Build trend analysis prompt
        StringBuilder trendData = new StringBuilder();
        trendData.append("Here are the last ")
                .append(Math.min(reports.size(), 3))
                .append(" reports for this patient, from newest to oldest:\n\n");

        for (int i = 0; i < Math.min(reports.size(), 3); i++) {
            HealthReport report = reports.get(i);
            trendData.append("Report ").append(i + 1)
                    .append(" (").append(report.getUploadedAt().toLocalDate()).append("):\n")
                    .append(report.getRawText().substring(0,
                            Math.min(500, report.getRawText().length())))
                    .append("\n\n");
        }

        String prompt = """
                You are a medical assistant analyzing health trends for an Indian patient.
                
                Compare these medical reports over time and:
                1. Identify which values are IMPROVING over time
                2. Identify which values are WORSENING over time
                3. Identify which values remain STABLE
                4. Give an overall health trend summary
                5. Suggest what the patient should urgently discuss with their doctor
                
                Keep the response friendly, simple and in the patient's interest.
                Flag any values that have been consistently abnormal across multiple reports.
                
                %s
                """.formatted(trendData.toString());

        return llmService.callLlm(prompt, user.getPreferredLanguage());
    }

    private void checkTrendsForUser(User user) {
        familyMemberRepository.findByUserId(user.getId()).forEach(member -> {
            List<HealthReport> reports = reportRepository
                    .findByFamilyMemberIdOrderByUploadedAtDesc(member.getId());

            if (reports.size() >= 2) {
                System.out.println(">>> Checking trends for: " + member.getFullName());
                List<String> concerns = detectConcerns(reports);
                if (!concerns.isEmpty()) {
                    alertService.sendTrendAlert(user, member.getFullName(), concerns);
                }
            }
        });
    }

    private List<String> detectConcerns(List<HealthReport> reports) {
        List<String> concerns = new ArrayList<>();
        String latestText = reports.get(0).getRawText().toLowerCase();

        // Check for consistently abnormal values
        if (latestText.contains("haemoglobin") && latestText.contains("low")) {
            concerns.add("Haemoglobin has been LOW in recent reports");
        }
        if (latestText.contains("vitamin b12") && latestText.contains("low")) {
            concerns.add("Vitamin B12 has been LOW in recent reports");
        }
        if (latestText.contains("vitamin d") && latestText.contains("low")) {
            concerns.add("Vitamin D has been LOW in recent reports");
        }
        if (latestText.contains("hba1c") && latestText.contains("high")) {
            concerns.add("HbA1c is HIGH — pre-diabetic range detected");
        }
        if (latestText.contains("cholesterol") && latestText.contains("high")) {
            concerns.add("Cholesterol levels have been HIGH");
        }
        if (latestText.contains("tsh") && latestText.contains("high")) {
            concerns.add("TSH is HIGH — possible thyroid issue");
        }
        return concerns;
    }
}