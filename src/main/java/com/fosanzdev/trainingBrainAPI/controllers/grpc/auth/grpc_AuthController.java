package com.fosanzdev.trainingBrainAPI.controllers.grpc.auth;

import com.fosanzdev.trainingBrainAPI.models.auth.AccessToken;
import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.auth.AuthCode;
import com.fosanzdev.trainingBrainAPI.models.auth.RefreshToken;
import com.fosanzdev.trainingBrainAPI.services.MailService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.auth.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.auth.IAuthService;
import com.fosanzdev.trainingBrainGrpcInterface.auth.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@GrpcService
public class grpc_AuthController extends AuthServiceGrpc.AuthServiceImplBase {

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;


    @Autowired
    private IAuthService authService;

    @Autowired
    private IAccountService accountService;

    @Override
    public void login(LoginRequest request, StreamObserver<Empty> responseObserver) {
        // Get the request parameters
        String username = request.getUsername();
        String password = request.getPassword();

        // Verify the account
        boolean validAccount = authService.validAccount(username, password, false);

        if (validAccount) {
            Account account = accountService.getAccountByUsername(username);
            authService.forceLogout(username);
            AuthCode code = authService.createAuthCode(username);

            //Run in new thread to avoid blocking the main thread
            new Thread(() -> {
                try {
                    MailService service = new MailService(emailUsername, emailPassword);
                    service.sendMail(account.getEmail(), "Código de verificación para TrainingBrain", "Tu código de verificación es: " + code.getCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            // Send the auth code to the client
            Empty response = Empty.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            // If the account is invalid, return an error
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid username or password");
            responseObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void verify(VerifyRequest request, StreamObserver<VerifyResponse> responseStreamObserver) {
        // Get the request parameters
        String username = request.getUsername();
        String password = request.getPassword();
        String authToken = request.getAuthToken();

        // Verify the auth code
        boolean validAccount = false;
        boolean validAuthCode = authService.validateAuthCode(authToken, username);
        if (validAuthCode) validAccount = authService.validAccount(username, password, true);

        if (validAuthCode && validAccount) {
            authService.invalidateAuthCode(authToken);
            RefreshToken refreshToken = authService.createRefreshToken(username);
            AccessToken accessToken = authService.createAccessToken(username);

            VerifyResponse response = VerifyResponse.newBuilder()
                    .setAccessToken(accessToken.getToken())
                    .setRefreshToken(refreshToken.getToken())
                    .build();

            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
        } else {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid auth code or account");
            responseStreamObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void refreshToken(RefreshTokenRequest request, StreamObserver<RefreshTokenResponse> responseStreamObserver) {
        // Get the request parameters
        String refreshToken = request.getRefreshToken();
        String accessToken = request.getAccessToken();

        // Validate the sent tokens
        boolean valid = authService.validateRefreshToken(refreshToken, accessToken);

        if (valid) {
            // Refresh the access token and return it
            AccessToken newAccessToken = authService.refreshAccessToken(refreshToken, accessToken);
            RefreshTokenResponse response = RefreshTokenResponse.newBuilder()
                    .setAccessToken(newAccessToken.getToken())
                    .build();

            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
        } else {
            // If the tokens are invalid, return an error
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid refresh token or access token");
            responseStreamObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<Empty> responseStreamObserver) {
        // Get the request parameters
        String username = request.getUsername();
        String name = request.getName();
        String password = request.getPassword();
        String email = request.getEmail();
        boolean professional = request.getProfessional();

        if (
            username.isEmpty() ||
            name.isEmpty() ||
            password.isEmpty() ||
            email.isEmpty()
        ) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            responseStreamObserver.onError(status.asRuntimeException());
            return;
        }

        // Register the user
        AuthCode authCode = authService.register(name, email, username, password, professional);

        // If authCode is not null, the account was created successfully
        if (authCode != null) {

            //Run in new thread to avoid blocking the main thread
            new Thread(() -> {
                try {
                    MailService service = new MailService(emailUsername, emailPassword);
                    service.sendMail(email, "Código de verificación para TrainingBrain", "Tu código de verificación es: " + authCode.getCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            // Send the OK response
            Empty response = Empty.newBuilder().build();
            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
        } else {
            // If authCode is null, the account already exists
            Status status = Status.ALREADY_EXISTS.withDescription("Account already exists");
            responseStreamObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void logout(LogoutRequest request, StreamObserver<LogoutResponse> responseStreamObserver) {
        // Get the request parameters
        String username = request.getUsername();
        String accessToken = request.getRefreshToken();

        boolean success = authService.logout(username, accessToken);

        if (success){
            LogoutResponse response = LogoutResponse.newBuilder()
                    .setLogout(true)
                    .build();
            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();

        } else {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid username or refresh token");
            responseStreamObserver.onError(status.asRuntimeException());
        }
    }
}
