package com.fosanzdev.trainingBrainAPI.controllers.grpc;

import com.fosanzdev.trainingBrainAPI.models.auth.AccessToken;
import com.fosanzdev.trainingBrainAPI.models.auth.AuthCode;
import com.fosanzdev.trainingBrainAPI.models.auth.RefreshToken;
import com.fosanzdev.trainingBrainAPI.services.interfaces.IAuthService;
import com.fosanzdev.trainingBrainGrpcInterface.auth.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class grpc_AuthController extends AuthServiceGrpc.AuthServiceImplBase {

    @Autowired
    private IAuthService authService;

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        // Get the request parameters
        String username = request.getUsername();
        String password = request.getPassword();

        // Verify the account
        boolean validAccount = authService.verifyAccount(username, password, false);

        if (validAccount) {
            authService.forceLogout(username);
            AuthCode code = authService.createAuthCode(username);

            // Send the auth code to the client
            LoginResponse response = LoginResponse.newBuilder()
                    .setAuthToken(code.getCode())
                    .build();
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
        boolean validAccount = authService.verifyAccount(username, password, true);
        boolean validAuthCode = authService.validateAuthCode(authToken, username);
        if (validAccount && validAuthCode) {
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
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseStreamObserver) {
        // Get the request parameters
        String username = request.getUsername();
        String name = request.getName();
        String password = request.getPassword();
        boolean professional = request.getProfessional();

        // Register the user
        AuthCode authCode = authService.register(name, username, password, professional);

        // If authCode is not null, the account was created successfully
        if (authCode != null) {
            // Send the auth code to the client
            RegisterResponse response = RegisterResponse.newBuilder()
                    .setAuthToken(authCode.getCode())
                    .build();
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
