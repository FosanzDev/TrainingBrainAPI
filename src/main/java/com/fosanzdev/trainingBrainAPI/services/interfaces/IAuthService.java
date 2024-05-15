package com.fosanzdev.trainingBrainAPI.services.interfaces;

import com.fosanzdev.trainingBrainAPI.models.auth.AccessToken;
import com.fosanzdev.trainingBrainAPI.models.auth.AuthCode;
import com.fosanzdev.trainingBrainAPI.models.auth.RefreshToken;

public interface IAuthService {

    // Basic user management
    public AuthCode register(String name, String username, String password, boolean professional);
    public boolean verifyAccount(String username, String password, boolean setValidated);
    public boolean logout(String username, String refreshToken);
    public boolean forceLogout(String username);

    //Token generation
    public AuthCode createAuthCode(String username);
    public AccessToken createAccessToken(String username);
    public RefreshToken createRefreshToken(String username);

    // Token validation
    public boolean validateAuthCode(String authCode, String username);
    public boolean validateAccessToken(String accessToken, String username);
    public boolean validateRefreshToken(String refreshToken, String accessToken);

    // Token invalidation
    public boolean invalidateAuthCode(String authCode);
    public boolean invalidateAccessToken(String accessToken);
    public boolean invalidateRefreshToken(String refreshToken);

    // Token refresh
    public AccessToken refreshAccessToken(String refreshToken, String accessToken);

}
