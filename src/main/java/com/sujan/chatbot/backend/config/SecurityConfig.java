package com.sujan.chatbot.backend.config;

import com.sujan.chatbot.backend.enums.UserRole;
import com.sujan.chatbot.backend.model.User;
import com.sujan.chatbot.backend.repository.UserRepository;
import com.sujan.chatbot.backend.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    // ─── Public endpoints — no token required ─────────────────────────────────
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",
            "/swagger-ui/**",
            "/api-docs/**",
            "/login/**",
            "/oauth2/**"
    };

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    public SecurityConfig(UserRepository userRepository,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    // ─── Main Security Filter Chain ───────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthFilter
    ) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // ── Disable CSRF — not needed for stateless JWT APIs ──────────────
                .csrf(AbstractHttpConfigurer::disable)

                // ── Authorization Rules ───────────────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority(UserRole.ROLE_ADMIN.name())
                        .anyRequest().authenticated()
                )

                // ── Stateless Session — JWT handles auth, no server-side sessions ─
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ── OAuth2 Google Login ────────────────────────────────────────────
                .oauth2Login(oauth2 -> oauth2
                        .successHandler((request, response, authentication) -> {

                            // ✅ Fix 5: Cast safely + null checks on all OIDC fields
                            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

                            String email    = oidcUser.getEmail();
                            String name     = oidcUser.getFullName();
                            String googleId = oidcUser.getSubject();

                            // Guard: email is required — reject if missing
                            if (email == null || email.isBlank()) {
                                response.sendRedirect(
                                        "http://localhost:4200/login?error=email_not_provided"
                                );
                                return;
                            }

                            // Use email as fallback name if fullName is null
                            String displayName = (name != null && !name.isBlank()) ? name : email;

                            // Find or create user in our database
                            User user = userRepository
                                    .findByProviderAndProviderId("google", googleId)
                                    .orElseGet(() -> userRepository
                                            .findByEmail(email)
                                            .orElseGet(() -> userRepository.save(
                                                    User.builder()
                                                            .email(email)
                                                            .name(displayName)
                                                            .role(UserRole.ROLE_USER)
                                                            .provider("google")
                                                            .providerId(googleId)
                                                            .build()
                                            ))
                                    );

                            // Generate tokens
                            String accessToken  = jwtService.generateAccessToken(user);
                            String refreshToken = jwtService.generateRefreshToken(user);

                            // Set refresh token as HttpOnly cookie
                            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
                            refreshCookie.setHttpOnly(true);
                            refreshCookie.setPath("/api/auth/refresh");
                            refreshCookie.setMaxAge(7 * 24 * 60 * 60);
                            response.addCookie(refreshCookie);

                            // Redirect to Angular with access token
                            response.sendRedirect(
                                    "http://localhost:4200/auth/callback?token=" + accessToken
                            );
                        })
                        .failureHandler((request, response, exception) ->
                                response.sendRedirect(
                                        "http://localhost:4200/login?error=oauth2_failed"
                                )
                        )
                )

                // ── JWT Filter runs before Spring's UsernamePassword filter ────────
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ─── Beans ────────────────────────────────────────────────────────────────

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                frontendUrl
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    /**
     * Loads users from the database by email.
     *  expression lambda (no braces, no return keyword)
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + username
                ));
    }

    /**
     *  Spring Security 7 changed DaoAuthenticationProvider.
     * UserDetailsService is now passed via CONSTRUCTOR (not setter).
     * PasswordEncoder is still set via setter.
     *
     * Old (Security 6.x):  new DaoAuthenticationProvider()
     *                       provider.setUserDetailsService(...)  ← removed in 7
     *
     * New (Security 7.x):  new DaoAuthenticationProvider(userDetailsService())
     *                       provider.setPasswordEncoder(...)     ← still works
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * BCrypt with work factor 10.
     * ~100ms per hash — deliberately slow to resist brute-force attacks.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Exposes AuthenticationManager as a bean for use in AuthController.
     * 'throws Exception' is required by the Spring API contract.
     *    IntelliJ warns it's never thrown — this is a false positive.
     *    We use @SuppressWarnings to silence it without hiding real issues.
     */
    @Bean
    @SuppressWarnings("java:S112")
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}