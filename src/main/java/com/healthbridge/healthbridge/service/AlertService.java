package com.healthbridge.healthbridge.service;

import com.healthbridge.healthbridge.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    public void sendTrendAlert(User user, String memberName, List<String> concerns) {
        // For now we print the alert — we will wire email later
        System.out.println("==========================================");
        System.out.println(">>> HEALTH ALERT for: " + user.getFullName());
        System.out.println(">>> Family Member: " + memberName);
        System.out.println(">>> Concerns detected:");
        concerns.forEach(c -> System.out.println("    - " + c));
        System.out.println(">>> Alert would be sent to: " + user.getEmail());
        System.out.println("==========================================");
    }
}