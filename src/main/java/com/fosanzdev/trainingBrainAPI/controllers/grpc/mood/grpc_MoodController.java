package com.fosanzdev.trainingBrainAPI.controllers.grpc.mood;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.auth.Account;
import com.fosanzdev.trainingBrainAPI.models.mood.AccountMood;
import com.fosanzdev.trainingBrainAPI.models.mood.Mood;
import com.fosanzdev.trainingBrainAPI.services.interfaces.auth.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.mood.IMoodService;
import com.fosanzdev.trainingBrainGrpcInterface.auth.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class grpc_MoodController extends MoodServiceGrpc.MoodServiceImplBase {

    @Autowired
    private IMoodService moodService;

    @Autowired
    private IAccountService accountService;


    @Override
    public void addMood(AddMoodRequest request, StreamObserver<Empty> responseStreamObserver) {
        String bearer = AuthInterceptor.getAuthorization();
        String token = bearer.split(" ")[1];
        Account account = accountService.getAccountByAccessToken(token);
        if (account == null) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid token");
            responseStreamObserver.onError(status.asRuntimeException());
        } else if (account.isProfessional()) {
            Status status = Status.PERMISSION_DENIED.withDescription("Professionals can't add moods");
            responseStreamObserver.onError(status.asRuntimeException());
        } else {
            try {
                moodService.addEntry(account.getId(), request.getMoodId());
                responseStreamObserver.onNext(Empty.newBuilder().build());
                responseStreamObserver.onCompleted();
            } catch (Exception e) {
                Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request, check moodId");
                responseStreamObserver.onError(status.asRuntimeException());
            }
        }
    }

    @Override
    public void getHistory(GetHistoryRequest request, StreamObserver<GetHistoryResponse> reponse) {
        String Bearer = AuthInterceptor.getAuthorization();
        String token = Bearer.split(" ")[1];
        Account account = accountService.getAccountByAccessToken(token);

        if (account == null) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid token");
            reponse.onError(status.asRuntimeException());
        } else if (account.isProfessional()) {
            Status status = Status.PERMISSION_DENIED.withDescription("Professionals can't get moods");
            reponse.onError(status.asRuntimeException());
        } else {
            List<AccountMood> history = moodService.getHistory(account.getId(), request.getLimit(), request.getOffset());
            reponse.onNext(toProtoHistoryResponse(history));
            reponse.onCompleted();
        }
    }

    @Override
    public void getHistoryById(GetHistoryByIdRequest request, StreamObserver<GetHistoryResponse> response){
        String Bearer = AuthInterceptor.getAuthorization();
        String token = Bearer.split(" ")[1];
        Account account = accountService.getAccountByAccessToken(token);

        if (account == null){
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid token");
            response.onError(status.asRuntimeException());
        } else if (!account.isProfessional()){
            Status status = Status.PERMISSION_DENIED.withDescription("Users can't get other users moods");
            response.onError(status.asRuntimeException());
        } else {
            List<AccountMood> history = moodService.getHistory(request.getId(), request.getLimit(), request.getOffset());
            response.onNext(toProtoHistoryResponse(history));
            response.onCompleted();
        }
    }

    @Override
    public void getMoods(Empty request, StreamObserver<GetMoodsResponse> response){
        List<Mood> moods = moodService.getMoods();
        response.onNext(toProtoMoodResponse(moods));
        response.onCompleted();
    }

    private GetMoodsResponse toProtoMoodResponse(List<Mood> moods){
        GetMoodsResponse.Builder response = GetMoodsResponse.newBuilder();
        for (Mood mood : moods){
            response.addMoods(toProto(mood));
        }
        return response.build();
    }

    private GetHistoryResponse toProtoHistoryResponse(List<AccountMood> history) {
        GetHistoryResponse.Builder response = GetHistoryResponse.newBuilder();
        for (AccountMood mood : history) {
            response.addAccountMoods(toProto(mood));
        }
        return response.build();
    }

    private com.fosanzdev.trainingBrainGrpcInterface.auth.AccountMood toProto(AccountMood accountMood) {
        return com.fosanzdev.trainingBrainGrpcInterface.auth.AccountMood.newBuilder()
                .setId(accountMood.getId())
                .setMood(toProto(accountMood.getMood()))
                .setDate(accountMood.getDate().toString())
                .build();
    }

    private com.fosanzdev.trainingBrainGrpcInterface.auth.Mood toProto (Mood mood){
        return com.fosanzdev.trainingBrainGrpcInterface.auth.Mood.newBuilder()
                .setId(mood.getId())
                .setDescription(mood.getDescription() == null ? "" : mood.getDescription())
                .setName(mood.getName() == null ? "" : mood.getName())
                .setColor(mood.getColor() == null ? "" : mood.getColor())
                .setIcon(mood.getIcon() == null ? "" : mood.getIcon())
                .build();
    }

}
