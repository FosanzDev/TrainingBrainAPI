package com.fosanzdev.trainingBrainAPI.controllers.grpc;

import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.services.interfaces.auth.IAccountService;
import com.fosanzdev.trainingBrainGrpcInterface.auth.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class grpc_AccountController extends AccountServiceGrpc.AccountServiceImplBase{

    @Autowired
    private IAccountService accountService;

    @Override
    public void me(Empty request, StreamObserver<MeResponse> responseStreamObserver) {
        String bearer = AuthInterceptor.getAuthorization();
        String token = bearer.split(" ")[1];
        if (token == null) {
            Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
            responseStreamObserver.onError(status.asRuntimeException());
        } else {
            Account account = accountService.getAccountByAccessToken(token);
            if (account == null){
                Status status = Status.INVALID_ARGUMENT.withDescription("Invalid token");
                responseStreamObserver.onError(status.asRuntimeException());
                return;
            }

            MeResponse response = MeResponse.newBuilder()
                    .setUsername(account.getUsername())
                    .setName(account.getName())
                    .setIsProfessional(account.isProfessional())
                    .setIsVerified(account.isVerified())
                    .setId(account.getId())
                    .build();

            responseStreamObserver.onNext(response);
            responseStreamObserver.onCompleted();
        }
    }

    @Override
    public void getAccountInfo(AccountInfoRequest request, StreamObserver<AccountInfoResponse> responseStreamObserver) {
        String bearer = AuthInterceptor.getAuthorization();
        String token = bearer.split(" ")[1];

        String id = request.getId();

        if (token == null) {
            Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
            responseStreamObserver.onError(status.asRuntimeException());
        } else {
            Account account = accountService.getAccountByAccessToken(token);
            if (account == null){
                Status status = Status.INVALID_ARGUMENT.withDescription("Invalid token");
                responseStreamObserver.onError(status.asRuntimeException());
                return;
            }

            if (account.getId().equals(id) || account.isProfessional()){
                Account accountById = accountService.getAccountById(id);
                if (accountById == null){
                    Status status = Status.INVALID_ARGUMENT.withDescription("Invalid account ID");
                    responseStreamObserver.onError(status.asRuntimeException());
                    return;
                }

                CompleteAccountInfo accountInfo = CompleteAccountInfo.newBuilder()
                        .setUsername(account.getUsername())
                        .setName(account.getName())
                        .setIsProfessional(account.isProfessional())
                        .setIsVerified(account.isVerified())
                        .setId(account.getId())
                        .build();

                AccountInfoResponse response = AccountInfoResponse.newBuilder()
                        .setCompleteAccountInfo(accountInfo)
                        .build();

                responseStreamObserver.onNext(response);
                responseStreamObserver.onCompleted();
            } else {
                CompleteAccountInfo accountInfo = CompleteAccountInfo.newBuilder()
                        .setUsername(account.getUsername())
                        .setName(account.getName())
                        .setIsProfessional(account.isProfessional())
                        .setIsVerified(account.isVerified())
                        .setId(account.getId())
                        .build();

                AccountInfoResponse response = AccountInfoResponse.newBuilder()
                        .setCompleteAccountInfo(accountInfo)
                        .build();

                responseStreamObserver.onNext(response);
                responseStreamObserver.onCompleted();
            }
        }
    }
}
