package com.fosanzdev.trainingBrainAPI.services.interfaces;

import com.fosanzdev.trainingBrainAPI.models.auth.AccessToken;
import com.fosanzdev.trainingBrainAPI.models.auth.AuthCode;
import com.fosanzdev.trainingBrainAPI.models.auth.RefreshToken;

public interface IAuthService {

    // Basic user management
    AuthCode register(String name, String username, String password, boolean professional);
    boolean validAccount(String username, String password, boolean alsoVerify);
    boolean logout(String username, String refreshToken);
    void forceLogout(String username);

    //Token generation
    AuthCode createAuthCode(String username);
    AccessToken createAccessToken(String username);
    RefreshToken createRefreshToken(String username);

    // Token validation
    boolean validateAuthCode(String authCode, String username);
    boolean validateRefreshToken(String refreshToken, String accessToken);

    // Token invalidation
    void invalidateAuthCode(String authCode);
    void invalidateAccessToken(String accessToken);
    void invalidateRefreshToken(String refreshToken);

    // Token refresh
    AccessToken refreshAccessToken(String refreshToken, String accessToken);

}
