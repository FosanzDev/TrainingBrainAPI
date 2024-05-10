package com.fosanzdev.trainingBrainAPI.controllers.grpc;

import com.fosanzdev.trainingBrainAPI.models.AuthCode;
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

    }

    @Override
    public void refreshToken(RefreshTokenRequest request, StreamObserver<RefreshTokenResponse> responseStreamObserver) {

    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseStreamObserver) {
        String username = request.getUsername();
        String name = request.getName();
        String password = request.getPassword();
        boolean success = authService.register(name, username, password);
        if (success) {
            AuthCode authCode = authService.createAuthCode(username);
            RegisterResponse response = RegisterResponse.newBuilder()
                    .setAuthToken(authCode.getCode())
                    .build();
            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
        } else {
            Status status = Status.ALREADY_EXISTS.withDescription("Account already exists");
            responseStreamObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void logout(LogoutRequest request, StreamObserver<LogoutResponse> responseStreamObserver) {

    }
}
