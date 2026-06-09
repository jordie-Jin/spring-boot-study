package com.sesac.ai.backend.security;

import com.sesac.ai.backend.domain.User;
import com.sesac.ai.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
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

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();
        String providerId = resolveProviderId(provider, oauthUser);
        String email = resolveEmail(provider, oauthUser, providerId);

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> createOAuthUser(provider, providerId, email));

        String token = jwtUtil.generate(user.getUsername(), user.getRole().name());
        response.sendRedirect(UriComponentsBuilder.fromUriString(frontendRedirectUri)
                .queryParam("token", token)
                .build()
                .toUriString());
    }

    private User createOAuthUser(String provider, String providerId, String email) {
        String username = userRepository.existsByUsername(email)
                ? fallbackUsername(provider, providerId)
                : email;

        return userRepository.save(User.oauthUser(username, provider, providerId));
    }

    private String fallbackUsername(String provider, String providerId) {
        return providerId + "@" + provider.toLowerCase() + ".oauth";
    }

    private String resolveProviderId(String provider, OAuth2User oauthUser) {
        if ("GOOGLE".equals(provider) && oauthUser instanceof OidcUser oidcUser) {
            return oidcUser.getSubject();
        }

        Object providerId = switch (provider) {
            case "GOOGLE" -> oauthUser.getAttribute("sub");
            case "KAKAO" -> oauthUser.getAttribute("id");
            default -> throw new IllegalArgumentException("Unsupported OAuth provider: " + provider);
        };

        return Objects.requireNonNull(providerId, provider + " OAuth user ID is missing").toString();
    }

    private String resolveEmail(String provider, OAuth2User oauthUser, String providerId) {
        String email = switch (provider) {
            case "GOOGLE" -> oauthUser instanceof OidcUser oidcUser
                    ? oidcUser.getEmail()
                    : oauthUser.getAttribute("email");
            case "KAKAO" -> kakaoEmail(oauthUser);
            default -> null;
        };

        return email == null || email.isBlank()
                ? fallbackUsername(provider, providerId)
                : email;
    }

    private String kakaoEmail(OAuth2User oauthUser) {
        Map<String, Object> kakaoAccount = oauthUser.getAttribute("kakao_account");
        if (kakaoAccount == null) {
            return null;
        }

        Object email = kakaoAccount.get("email");
        return email == null ? null : email.toString();
    }
}
