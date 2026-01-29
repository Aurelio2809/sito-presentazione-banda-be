package org.example.sitopresentazionebandabenew.service.impl;

import org.example.sitopresentazionebandabenew.dto.requests.LoginRequest;
import org.example.sitopresentazionebandabenew.dto.responses.UserResponse;
import org.example.sitopresentazionebandabenew.entity.User;
import org.example.sitopresentazionebandabenew.exception.UnauthorizedException;
import org.example.sitopresentazionebandabenew.mapper.UserMapper;
import org.example.sitopresentazionebandabenew.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
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
}
