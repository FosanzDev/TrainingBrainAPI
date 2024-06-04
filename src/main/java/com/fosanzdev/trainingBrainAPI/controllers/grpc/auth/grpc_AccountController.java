package com.fosanzdev.trainingBrainAPI.controllers.grpc.auth;

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

    private CompleteAccountInfo buildCompleteAccountInfo(Account account){
        return CompleteAccountInfo.newBuilder()
                .setUsername(account.getUsername() == null ? "" : account.getUsername())
                .setName(account.getName() == null ? "" : account.getName())
                .setEmail(account.getEmail() == null ? "" : account.getEmail())
                .setIsProfessional(account.isProfessional())
                .setIsVerified(account.isVerified())
                .setRelatedId(account.isProfessional() ? account.getProfessionalDetails().getId() : account.getUserDetails().getId())
                .setId(account.getId() == null ? "" : account.getId())
                .build();
    }

    private BasicAccountInfo buildBasicAccountInfo(Account account){
        return BasicAccountInfo.newBuilder()
                .setUsername(account.getUsername() == null ? "" : account.getUsername())
                .setName(account.getName() == null ? "" : account.getName())
                .setIsProfessional(account.isProfessional())
                .setRelatedId(account.isProfessional() ? account.getProfessionalDetails().getId() : account.getUserDetails().getId())
                .setId(account.getId() == null ? "" : account.getId())
                .build();
    }

    @Override
    public void me(Empty request, StreamObserver<CompleteAccountInfo> responseStreamObserver) {
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

            responseStreamObserver.onNext(buildCompleteAccountInfo(account));
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

                AccountInfoResponse response = AccountInfoResponse.newBuilder()
                        .setCompleteAccountInfo(buildCompleteAccountInfo(accountById))
                        .build();

                responseStreamObserver.onNext(response);
                responseStreamObserver.onCompleted();
            } else {

                AccountInfoResponse response = AccountInfoResponse.newBuilder()
                        .setBasicAccountInfo(buildBasicAccountInfo(account))
                        .build();

                responseStreamObserver.onNext(response);
                responseStreamObserver.onCompleted();
            }
        }
    }

    @Override
    public void updateAccountInfo(UpdateAccountRequest request, StreamObserver<Empty> response){
        String bearer = AuthInterceptor.getAuthorization();
        String token = bearer.split(" ")[1];

        Account accountToUpdate = accountService.getAccountByAccessToken(token);
        if (accountToUpdate == null) {
            Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
            response.onError(status.asRuntimeException());
            return;
        }

        try{
            Account account = new Account();
            account.setName(request.getName());
            account.setEmail(request.getEmail());
            account.setPassword(request.getPassword());
            account.setUsername(request.getUsername());
            accountService.updateAccount(accountToUpdate, account);
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid account data");
            response.onError(status.asRuntimeException());
            return;
        }

        response.onNext(Empty.newBuilder().build());
        response.onCompleted();
    }
}
