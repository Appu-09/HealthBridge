package com.healthbridge.healthbridge.repository;

import com.healthbridge.healthbridge.model.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {

    List<FamilyMember> findByUserId(Long userId);

    Optional<FamilyMember> findByIdAndUserId(Long id, Long userId);
}