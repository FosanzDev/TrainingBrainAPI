package com.fosanzdev.trainingBrainAPI.services.auth.interfaces;

import com.fosanzdev.trainingBrainAPI.models.AccessToken;
import com.fosanzdev.trainingBrainAPI.models.AuthCode;
import com.fosanzdev.trainingBrainAPI.models.RefreshToken;

public interface IAuthService {

    // Basic user management
    public boolean register(String name, String username, String password);

    //Token generation
    public AuthCode createAuthCode(String username);
    public AccessToken createAccessToken(String username);
    public RefreshToken createRefreshToken(String username);

    // Token validation
    public boolean validateAuthCode(String authCode, String username);
    public boolean validateAccessToken(String accessToken, String username);
    public boolean validateRefreshToken(String refreshToken, String username);

    // Token invalidation
    public boolean invalidateAuthCode(String authCode);
    public boolean invalidateAccessToken(String accessToken);
    public boolean invalidateRefreshToken(String refreshToken);

    // Token refresh
    public AccessToken refreshAccessToken(String refreshToken, String accessToken);

}
