package com.healthbridge.healthbridge.service;

import com.healthbridge.healthbridge.dto.request.FamilyMemberRequest;
import com.healthbridge.healthbridge.dto.response.FamilyMemberResponse;
import com.healthbridge.healthbridge.model.FamilyMember;
import com.healthbridge.healthbridge.model.User;
import com.healthbridge.healthbridge.repository.FamilyMemberRepository;
import com.healthbridge.healthbridge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyMemberService {

    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;

    public FamilyMemberResponse addMember(String email, FamilyMemberRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FamilyMember member = FamilyMember.builder()
                .fullName(request.getFullName())
                .relationship(request.getRelationship())
                .age(request.getAge())
                .gender(request.getGender())
                .user(user)
                .build();

        FamilyMember saved = familyMemberRepository.save(member);
        return mapToResponse(saved);
    }

    public List<FamilyMemberResponse> getAllMembers(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return familyMemberRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public FamilyMemberResponse getMember(String email, Long memberId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FamilyMember member = familyMemberRepository
                .findByIdAndUserId(memberId, user.getId())
                .orElseThrow(() -> new RuntimeException("Family member not found"));

        return mapToResponse(member);
    }

    public void deleteMember(String email, Long memberId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FamilyMember member = familyMemberRepository
                .findByIdAndUserId(memberId, user.getId())
                .orElseThrow(() -> new RuntimeException("Family member not found"));

        familyMemberRepository.delete(member);
    }

    private FamilyMemberResponse mapToResponse(FamilyMember member) {
        return FamilyMemberResponse.builder()
                .id(member.getId())
                .fullName(member.getFullName())
                .relationship(member.getRelationship())
                .age(member.getAge())
                .gender(member.getGender())
                .createdAt(member.getCreatedAt())
                .build();
    }
}