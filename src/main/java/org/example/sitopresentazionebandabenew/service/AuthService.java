package org.example.sitopresentazionebandabenew.service;

import org.example.sitopresentazionebandabenew.dto.requests.ChangePasswordRequest;
import org.example.sitopresentazionebandabenew.dto.requests.LoginRequest;
import org.example.sitopresentazionebandabenew.dto.requests.UpdateProfileRequest;
import org.example.sitopresentazionebandabenew.dto.responses.UserResponse;

public interface AuthService {

    UserResponse login(LoginRequest request);

    UserResponse getCurrentUser();
    
    UserResponse updateProfile(UpdateProfileRequest request);
    
    void changePassword(ChangePasswordRequest request);
}
