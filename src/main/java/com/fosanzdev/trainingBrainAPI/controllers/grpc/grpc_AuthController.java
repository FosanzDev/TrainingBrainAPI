package com.fosanzdev.trainingBrainAPI.controllers.grpc;

import com.fosanzdev.trainingBrainAPI.models.AccessToken;
import com.fosanzdev.trainingBrainAPI.models.Account;
import com.fosanzdev.trainingBrainAPI.models.AuthCode;
import com.fosanzdev.trainingBrainAPI.models.RefreshToken;
import com.fosanzdev.trainingBrainAPI.services.auth.AuthService;
import com.fosanzdev.trainingBrainAPI.services.auth.interfaces.IAuthService;
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
        LoginResponse response = LoginResponse.newBuilder()
                .setAuthToken(authService.createAuthCode(request.getUsername()).getCode())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void verify(VerifyRequest request, StreamObserver<VerifyResponse> responseStreamObserver) {
        // Get the request parameters
        String username = request.getUsername();
        String authToken = request.getAuthToken();

        // Verify the auth code
        boolean valid = authService.validateAuthCode(authToken, username);
        if (valid) {
            authService.verifyAccount(username);
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

    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseStreamObserver) {
        // Get the request parameters
        String username = request.getUsername();
        String name = request.getName();
        String password = request.getPassword();

        // Register the user
        AuthCode authCode = authService.register(name, username, password);

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

    }
}
