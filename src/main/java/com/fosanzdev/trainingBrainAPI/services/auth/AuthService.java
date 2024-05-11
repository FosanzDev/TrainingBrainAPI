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


    @Transactional
    @Override
    public AuthCode register(String name, String username, String password) {
        Account account = accountRepository.findByUsername(username);

        if (account != null) {
            if (account.isVerified()){
                //Account already exists
                return null;
            } else {
                //Account exists but is not verified
                //Regenerate auth code for verification
                return createAuthCode(username);
            }
        }

        //Account does not exist
        //Create new account
        account = new Account();
        account.setUsername(username);
        account.setPassword(password.hashCode() + "");
        account.setVerified(false);
        accountRepository.save(account);

        //Generate auth code for verification
        return createAuthCode(username);
    }

    @Transactional
    @Override
    public boolean verifyAccount(String username) {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            //Account not found
            return false;
        }

        account.setVerified(true);
        accountRepository.save(account);
        return true;
    }

    @Transactional
    @Override
    public boolean logout(String username, String refreshToken) {
        //Find account
        Account account = accountRepository.findByUsername(username);
        if (account == null) return false;

        //Check if refresh token belongs to account
        RefreshToken token = refreshTokenRepository.find(refreshToken);
        if (token == null) return false;

        if (token.getAccount().getId().equals(account.getId())) {
            // Token belongs to account
            invalidateAllAccessTokens(account);
            invalidateRefreshToken(refreshToken);
            return true;
        }

        //Token does not belong to account
        return false;
    }

    /**
     * Create a new auth code for an account
     * @param username Username of account to create auth code for
     * @return Auth code
     */
    @Transactional
    @Override
    public AuthCode createAuthCode(String username) {
        //Find account
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            //Account not found
            return null;
        }

        // Clear all existing auth codes
        invalidateAllAuthCodes(account);


        //Create new auth code
        AuthCode authCode = new AuthCode();
        authCode.setAccount(account);
        authCodeRepository.save(authCode);

        //Return auth code
        return authCode;
    }

    /**
     * Clear all auth codes for an account
     * @param account Account to clear auth codes for
     */
    @Transactional
    public void invalidateAllAuthCodes(Account account) {
        List<AuthCode> authCodes = authCodeRepository.findByAccount(account.getId());
        authCodeRepository.deleteAll(authCodes);
    }

    @Transactional
    @Override
    public AccessToken createAccessToken(String username) {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            System.out.println("Account not found");
            return null;
        }

        invalidateAllAccessTokens(account);

        AccessToken accessToken = new AccessToken();
        accessToken.setAccount(account);
        accessTokenRepository.save(accessToken);

        return accessToken;
    }

    @Transactional
    public void invalidateAllAccessTokens(Account account) {
        List<AccessToken> accessTokens = accessTokenRepository.findByAccount(account.getId());
        accessTokenRepository.deleteAll(accessTokens);
    }

    @Transactional
    @Override
    public RefreshToken createRefreshToken(String username) {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            System.out.println("Account not found");
            return null;
        }

        invalidateAllRefreshTokens(account);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAccount(account);
        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Transactional
    public void invalidateAllRefreshTokens(Account account) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findByAccount(account.getId());
        refreshTokenRepository.deleteAll(refreshTokens);
    }

    /**
     * Validate an auth code
     * @param authCode Auth code to validate
     * @param username Username of account to validate auth code for
     * @return True if auth code is valid, false otherwise
     */
    @Transactional
    @Override
    public boolean validateAuthCode(String authCode, String username) {
        //Find account
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            //Account not found
            return false;
        }

        //Find auth code
        AuthCode code = authCodeRepository.find(authCode);
        if (code == null) {
            //Auth code not found
            return false;
        }

        //Check if auth code is valid and belongs to the account
        return code.getAccount().getId().equals(account.getId());
    }

    @Override
    public boolean validateAccessToken(String accessToken, String username) {
        return false;
    }

    @Override
    public boolean validateRefreshToken(String refreshToken, String username) {
        Account account = accountRepository.findByUsername(username);
        if (account == null) return false;

        RefreshToken token = refreshTokenRepository.find(refreshToken);
        if (token == null) return false;

        return token.getAccount().getId().equals(account.getId());
    }

    /**
     * Invalidate an auth code
     * @param authCode Auth code to invalidate
     * @return True if auth code was invalidated, false otherwise
     */
    @Transactional
    @Override
    public boolean invalidateAuthCode(String authCode) {
        AuthCode code = authCodeRepository.find(authCode);
        if (code == null) return false;

        authCodeRepository.delete(code);
        return true;
    }

    @Override
    public boolean invalidateAccessToken(String accessToken) {
        AccessToken token = accessTokenRepository.find(accessToken);
        if (token == null) return false;

        accessTokenRepository.delete(token);
        return true;
    }

    @Override
    public boolean invalidateRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.find(refreshToken);
        if (token == null) return false;

        refreshTokenRepository.delete(token);
        return true;
    }

    @Transactional
    @Override
    public AccessToken refreshAccessToken(String refreshToken, String accessToken) {
        invalidateAccessToken(accessToken);
        AccessToken newAccessToken = new AccessToken();
        newAccessToken.setAccount(refreshTokenRepository.find(refreshToken).getAccount());
        return accessTokenRepository.save(newAccessToken);
    }
}
