package com.sesac.ai.backend.security;

import com.sesac.ai.backend.domain.User;
import com.sesac.ai.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {

    private static final String SOCIAL_LOGIN_PASSWORD = "{noop}SOCIAL_LOGIN_ONLY";

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        String password = user.getPasswordHash() != null
                ? user.getPasswordHash()
                : SOCIAL_LOGIN_PASSWORD;

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                password,
                List.of(authority)
        );
    }
}
