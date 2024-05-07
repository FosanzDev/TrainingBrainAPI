package com.fosanzdev.trainingBrainAPI.controllers.grpc;

import com.fosanzdev.trainingBrainGrpcInterface.auth.AuthServiceGrpc;
import com.fosanzdev.trainingBrainGrpcInterface.auth.LoginRequest;
import com.fosanzdev.trainingBrainGrpcInterface.auth.LoginResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class grpc_AuthController extends AuthServiceGrpc.AuthServiceImplBase {

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        LoginResponse response = LoginResponse.newBuilder()
                .setAuthToken("authToken")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
