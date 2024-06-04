package com.fosanzdev.trainingBrainAPI.controllers.grpc.data;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.data.User;
import com.fosanzdev.trainingBrainAPI.services.interfaces.auth.IAccountService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
import com.fosanzdev.trainingBrainGrpcInterface.data.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@GrpcService
public class grpc_UserDataController extends UserDataServiceGrpc.UserDataServiceImplBase{

    @Autowired
    private IUserDataService userDataService;

    private PrivateUserDataResponse buildPrivateUserDataResponse(User user){
        PrivateUserDataResponse.Builder privateUserDataResponseBuilder = PrivateUserDataResponse.newBuilder();
        privateUserDataResponseBuilder.setId(user.getId() == null ? "" : user.getId());
        privateUserDataResponseBuilder.setPublicBio(user.getPublicBio() == null ? "" : user.getPublicBio());
        privateUserDataResponseBuilder.setPrivateBio(user.getPrivateBio() == null ? "" : user.getPrivateBio());
        privateUserDataResponseBuilder.setHistory(user.getHistory() == null ? "" : user.getHistory());
        privateUserDataResponseBuilder.setDateOfBirth(user.getDateOfBirth() == null ? "" : user.getDateOfBirth());
        return privateUserDataResponseBuilder.build();
    }

    private PublicUserDataResponse buildPublicUserDataResponse(User user){
        PublicUserDataResponse.Builder publicUserDataResponseBuilder = PublicUserDataResponse.newBuilder();
        publicUserDataResponseBuilder.setId(user.getId() == null ? "" : user.getId());
        publicUserDataResponseBuilder.setPublicBio(user.getPublicBio() == null ? "" : user.getPublicBio());
        publicUserDataResponseBuilder.setDateOfBirth(user.getDateOfBirth() == null ? "" : user.getDateOfBirth());
        return publicUserDataResponseBuilder.build();
    }

    @Override
    public void getMyData(Empty request, StreamObserver<PrivateUserDataResponse> response){
        try{

            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null){
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            response.onNext(buildPrivateUserDataResponse(user));
            response.onCompleted();

        } catch (Exception e){
            Status status = Status.INTERNAL.withDescription("Error getting user data");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getUserDataById(GetUserDataByIdRequest request, StreamObserver<UserDataResponse> response){
        try{
            User user = userDataService.getUserById(request.getId());
            if (user == null){
                Status status = Status.NOT_FOUND.withDescription("User not found");
                response.onError(status.asRuntimeException());
                return;
            }

            try{
                String bearer = AuthInterceptor.getAuthorization();
                String token = bearer.split(" ")[1];
                User currentUser = userDataService.getUserByAccessToken(token);
                if (currentUser != null){
                    if (Objects.equals(user.getId(), currentUser.getId())){
                        UserDataResponse.Builder userDataResponseBuilder = UserDataResponse.newBuilder();
                        userDataResponseBuilder.setPrivateData(buildPrivateUserDataResponse(user));

                        response.onNext(userDataResponseBuilder.build());
                        response.onCompleted();
                    }
                }
            } catch (Exception e){
                UserDataResponse.Builder userDataResponseBuilder = UserDataResponse.newBuilder();
                userDataResponseBuilder.setPublicData(buildPublicUserDataResponse(user));

                response.onNext(userDataResponseBuilder.build());
                response.onCompleted();
            }

        } catch (Exception e){
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            response.onError(status.asRuntimeException());
        }
    }


    @Override
    public void updateUserData(UpdateUserDataRequest request, StreamObserver<Empty> response){
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];

            User userToUpdate = userDataService.getUserByAccessToken(token);
            if (userToUpdate == null){
                Status status = Status.NOT_FOUND.withDescription("User not found");
                response.onError(status.asRuntimeException());
                return;
            }

            User user = new User();
            user.setPublicBio(request.getPublicBio());
            user.setPrivateBio(request.getPrivateBio());
            user.setHistory(request.getHistory());
            user.setDateOfBirth(request.getDateOfBirth());

            try{
                userDataService.updateUser(userToUpdate, user);
            } catch (Exception e){
                Status status = Status.INVALID_ARGUMENT.withDescription("Invalid date format");
                response.onError(status.asRuntimeException());
                return;
            }

            response.onNext(Empty.newBuilder().build());
            response.onCompleted();

        } catch (Exception e){
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            response.onError(status.asRuntimeException());
        }
    }
}
