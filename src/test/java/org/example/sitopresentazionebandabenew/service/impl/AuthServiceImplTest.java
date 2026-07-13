package org.example.sitopresentazionebandabenew.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.sitopresentazionebandabenew.dto.requests.ChangePasswordRequest;
import org.example.sitopresentazionebandabenew.dto.requests.LoginRequest;
import org.example.sitopresentazionebandabenew.dto.responses.UserResponse;
import org.example.sitopresentazionebandabenew.entity.User;
import org.example.sitopresentazionebandabenew.exception.BadRequestException;
import org.example.sitopresentazionebandabenew.mapper.UserMapper;
import org.example.sitopresentazionebandabenew.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    private AuthServiceImpl service;
    private User user;

    @BeforeEach
    void setUp() {
        service = new AuthServiceImpl(authenticationManager, userMapper, userRepository, passwordEncoder);
        user = User.builder()
                .id(7L)
                .username("admin")
                .email("admin@example.it")
                .password("encoded-old")
                .role(User.Role.ADMIN)
                .enabled(true)
                .build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loginAuthenticatesAndStoresTheSecurityContext() {
        LoginRequest request = new LoginRequest("admin", "secret");
        UserResponse response = UserResponse.builder().id(7L).username("admin").build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = service.login(request);

        assertThat(result).isSameAs(response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);
    }

    @Test
    void rejectsAnIncorrectCurrentPassword() {
        authenticateUser();
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-old")).thenReturn(false);

        assertThatThrownBy(() -> service.changePassword(new ChangePasswordRequest("wrong", "new-secret")))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Password attuale non corretta");
        verify(userRepository, never()).save(any());
    }

    @Test
    void encodesAndPersistsAValidNewPassword() {
        authenticateUser();
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-secret", "encoded-old")).thenReturn(true);
        when(passwordEncoder.encode("new-secret")).thenReturn("encoded-new");

        service.changePassword(new ChangePasswordRequest("old-secret", "new-secret"));

        assertThat(user.getPassword()).isEqualTo("encoded-new");
        verify(userRepository).save(user);
    }

    private void authenticateUser() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
