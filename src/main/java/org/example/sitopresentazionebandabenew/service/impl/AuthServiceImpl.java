package org.example.sitopresentazionebandabenew.service.impl;

import org.example.sitopresentazionebandabenew.dto.requests.ChangePasswordRequest;
import org.example.sitopresentazionebandabenew.dto.requests.LoginRequest;
import org.example.sitopresentazionebandabenew.dto.requests.UpdateProfileRequest;
import org.example.sitopresentazionebandabenew.dto.responses.UserResponse;
import org.example.sitopresentazionebandabenew.entity.User;
import org.example.sitopresentazionebandabenew.exception.BadRequestException;
import org.example.sitopresentazionebandabenew.exception.UnauthorizedException;
import org.example.sitopresentazionebandabenew.mapper.UserMapper;
import org.example.sitopresentazionebandabenew.repository.UserRepository;
import org.example.sitopresentazionebandabenew.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager, 
            UserMapper userMapper,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = (User) authentication.getPrincipal();
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() 
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException("Utente non autenticato");
        }
        
        User user = (User) authentication.getPrincipal();
        return userMapper.toResponse(user);
    }
    
    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User currentUser = getAuthenticatedUser();
        
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Verifica se l'email è già in uso da un altro utente
            userRepository.findByEmail(request.getEmail())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(currentUser.getId())) {
                        throw new BadRequestException("Email già in uso");
                    }
                });
            currentUser.setEmail(request.getEmail());
        }
        
        User savedUser = userRepository.save(currentUser);
        return userMapper.toResponse(savedUser);
    }
    
    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = getAuthenticatedUser();
        
        // Verifica che la password attuale sia corretta
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Password attuale non corretta");
        }
        
        // Imposta la nuova password (codificata)
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }
    
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() 
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException("Utente non autenticato");
        }
        
        // Recupera l'utente aggiornato dal DB
        User principal = (User) authentication.getPrincipal();
        return userRepository.findById(principal.getId())
            .orElseThrow(() -> new UnauthorizedException("Utente non trovato"));
    }
}
