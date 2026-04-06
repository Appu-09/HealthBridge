package com.healthbridge.healthbridge.config;

import com.healthbridge.healthbridge.repository.UserRepository;
import com.healthbridge.healthbridge.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        System.out.println(">>> AUTH HEADER: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println(">>> NO TOKEN FOUND - skipping filter");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        System.out.println(">>> TOKEN: " + token);

        String email = null;
        try {
            email = jwtUtil.extractEmail(token);
            System.out.println(">>> EMAIL FROM TOKEN: " + email);
        } catch (Exception e) {
            System.out.println(">>> FAILED TO EXTRACT EMAIL: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            boolean userExists = userRepository.existsByEmail(email);
            System.out.println(">>> USER EXISTS IN DB: " + userExists);

            boolean tokenValid = jwtUtil.isTokenValid(token, email);
            System.out.println(">>> TOKEN VALID: " + tokenValid);

            if (userExists && tokenValid) {
                UserDetails userDetails = User.builder()
                        .username(email)
                        .password("")
                        .authorities(new ArrayList<>())
                        .build();

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println(">>> AUTHENTICATION SET SUCCESSFULLY");
            } else {
                System.out.println(">>> AUTHENTICATION FAILED - userExists: " + userExists + ", tokenValid: " + tokenValid);
            }
        }

        filterChain.doFilter(request, response);
    }
}