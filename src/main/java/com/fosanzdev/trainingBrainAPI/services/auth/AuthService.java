package com.fosanzdev.trainingBrainAPI.services.auth;

import com.fosanzdev.trainingBrainAPI.models.AccessToken;
import com.fosanzdev.trainingBrainAPI.models.Account;
import com.fosanzdev.trainingBrainAPI.models.AuthCode;
import com.fosanzdev.trainingBrainAPI.models.RefreshToken;
import com.fosanzdev.trainingBrainAPI.repositories.auth.*;
import com.fosanzdev.trainingBrainAPI.services.auth.interfaces.IAuthService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private AuthCodeRepository authCodeRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public boolean register(String name, String username, String password) {
        Account account = accountRepository.findByUsername(username);
        if (account != null) {
            System.out.println("Account already exists");
            return false;
        }

        account = new Account();
        account.setUsername(username);
        account.setPassword(password.hashCode() + "");
        accountRepository.save(account);

        return true;
    }

    @Transactional
    @Override
    public AuthCode createAuthCode(String username) {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            System.out.println("Account not found");
            return null;
        }

        clearAuthCodes(account);

        AuthCode authCode = new AuthCode();
        authCode.setAccount(account);
        authCodeRepository.save(authCode);

        return authCode;
    }

    @Transactional
    public void clearAuthCodes(Account account) {
        List<AuthCode> authCodes = authCodeRepository.findByAccount(account.getId());
        authCodeRepository.deleteAll(authCodes);
    }

    @Override
    public AccessToken createAccessToken(String username) {
        return null;
    }

    @Override
    public RefreshToken createRefreshToken(String username) {
        return null;
    }

    @Override
    public boolean validateAuthCode(String authCode, String username) {
        return false;
    }

    @Override
    public boolean validateAccessToken(String accessToken, String username) {
        return false;
    }

    @Override
    public boolean validateRefreshToken(String refreshToken, String username) {
        return false;
    }

    @Override
    public boolean invalidateAuthCode(String authCode) {
        return false;
    }

    @Override
    public boolean invalidateAccessToken(String accessToken) {
        return false;
    }

    @Override
    public boolean invalidateRefreshToken(String refreshToken) {
        return false;
    }

    @Override
    public AccessToken refreshAccessToken(String refreshToken, String accessToken) {
        return null;
    }
}
