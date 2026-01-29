package org.example.sitopresentazionebandabenew.service;

import org.example.sitopresentazionebandabenew.dto.requests.LoginRequest;
import org.example.sitopresentazionebandabenew.dto.responses.UserResponse;

public interface AuthService {

    UserResponse login(LoginRequest request);

    UserResponse getCurrentUser();
}
