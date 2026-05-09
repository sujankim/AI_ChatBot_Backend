package com.sujan.chatbot.backend.service;

import com.sujan.chatbot.backend.dto.request.LoginRequest;
import com.sujan.chatbot.backend.dto.request.RegisterRequest;
import com.sujan.chatbot.backend.dto.response.AuthResponse;
import com.sujan.chatbot.backend.dto.response.UserResponse;
import com.sujan.chatbot.backend.enums.UserRole;
import com.sujan.chatbot.backend.exception.EmailAlreadyExistsException;
import com.sujan.chatbot.backend.exception.InvalidCredentialsException;
import com.sujan.chatbot.backend.model.User;
import com.sujan.chatbot.backend.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user with email/password.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // 2. Create and save the user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))  // NEVER store plain text
                .role(UserRole.ROLE_USER)
                .provider("local")
                .build();

        User savedUser = userRepository.save(user);

        // 3. Generate tokens
        return buildAuthResponse(savedUser);
    }

    /**
     * Authenticate user with email/password.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        // 2. Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // 3. Generate tokens
        return buildAuthResponse(user);
    }

    /**
     * Validate a refresh token and issue a new access token.
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        // 1. Validate the refresh token
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new InvalidCredentialsException();
        }

        // 2. Check it's actually a refresh token (not an access token)
        String tokenType = jwtService.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new InvalidCredentialsException();
        }

        // 3. Load the user
        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        // 4. Issue new access token (refresh token stays the same)
        return buildAuthResponse(user);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiry() / 1000)   // Convert to seconds
                .user(UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .provider(user.getProvider())
                        .build())
                .build();
    }

    /**
     * Generate a refresh token for a user.
     * Called by controller to set HttpOnly cookie.
     */
    public String generateRefreshToken(User user) {
        return jwtService.generateRefreshToken(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new com.sujan.chatbot.backend.exception.UserNotFoundException(email));
    }
}
