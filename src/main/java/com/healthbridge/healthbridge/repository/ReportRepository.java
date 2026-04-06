package com.healthbridge.healthbridge.repository;

import com.healthbridge.healthbridge.model.HealthReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<HealthReport, Long> {

    List<HealthReport> findByFamilyMemberIdOrderByUploadedAtDesc(Long familyMemberId);

    Optional<HealthReport> findByIdAndFamilyMemberId(Long id, Long familyMemberId);
}