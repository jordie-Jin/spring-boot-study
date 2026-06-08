package com.sesac.ai.backend.security;

import com.sesac.ai.backend.domain.User;
import com.sesac.ai.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.oauth2.frontend-redirect-uri:http://localhost:5173/oauth/callback}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String providerId = resolveProviderId(oauthUser);
        String email = resolveEmail(oauthUser, providerId);

        User user = userRepository.findByProviderAndProviderId("GOOGLE", providerId)
                .or(() -> userRepository.findByUsername(email))
                .orElseGet(() -> userRepository.save(User.oauthUser(email, providerId)));

        String token = jwtUtil.generate(user.getUsername(), user.getRole().name());
        response.sendRedirect(UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("token", token)
                .build()
                .toUriString());
    }

    private String resolveProviderId(OAuth2User oauthUser) {
        if (oauthUser instanceof OidcUser oidcUser) {
            return oidcUser.getSubject();
        }

        return Objects.requireNonNull(oauthUser.getAttribute("sub"), "Google OAuth subject is missing");
    }

    private String resolveEmail(OAuth2User oauthUser, String providerId) {
        String email = oauthUser instanceof OidcUser oidcUser
                ? oidcUser.getEmail()
                : oauthUser.getAttribute("email");

        return email == null || email.isBlank()
                ? providerId + "@google.oauth"
                : email;
    }
}
