package com.fosanzdev.trainingBrainAPI.controllers.grpc.data;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.data.Opinion;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.User;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IOpinionService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
import com.fosanzdev.trainingBrainGrpcInterface.data.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class grpc_OpinionController extends OpinionServiceGrpc.OpinionServiceImplBase {

    @Autowired
    private IOpinionService opinionService;

    @Autowired
    private IUserDataService userDataService;

    @Autowired
    private IProDataService proDataService;

    public OpinionResponse buildOpinionResponse(Opinion opinion){
        return OpinionResponse.newBuilder()
                .setId(opinion.getId())
                .setTitle(opinion.getTitle())
                .setContent(opinion.getDescription())
                .setRating(opinion.getRating())
                .build();
    }

    @Override
    public void createOpinion(CreateOpinionRequest request, StreamObserver<Empty> response){
        Professional professional = proDataService.getProfessionalById(request.getProfesionalId());
        if (professional == null){
            Status status = Status.NOT_FOUND.withDescription("Professional not found");
            response.onError(status.asRuntimeException());
            return;
        }

        String bearer = AuthInterceptor.getAuthorization();
        String token = bearer.split(" ")[1];
        User user = userDataService.getUserByAccessToken(token);
        if (user == null){
            Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
            response.onError(status.asRuntimeException());
            return;
        }

        Opinion opinion = new Opinion();
        opinion.setTitle(request.getTitle());
        opinion.setDescription(request.getContent());
        opinion.setRating(request.getRating());

        if (opinionService.createOpinion(user, professional, opinion)){
            response.onNext(Empty.newBuilder().build());
            response.onCompleted();
        } else {
            Status status = Status.INTERNAL.withDescription("Error creating opinion");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void deleteOpinion(DeleteOpinionRequest request, StreamObserver<Empty> response){
        try{
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null){
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            if (opinionService.deleteOpinion(user, request.getId())){
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.INTERNAL.withDescription("Error deleting opinion");
                response.onError(status.asRuntimeException());
            }

        } catch (Exception e){
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getMyOpinions(Empty request, StreamObserver<OpinionList> response){
        try{
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            Professional professional = proDataService.getProfessionalByAccessToken(token);

            if (user == null){
                if (professional == null){
                    Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                    response.onError(status.asRuntimeException());

                } else {
                    OpinionList.Builder opinionList = OpinionList.newBuilder();
                    opinionService.getMyOpinions(professional).forEach(opinion -> opinionList.addOpinions(
                            buildOpinionResponse(opinion)
                    ));
                    response.onNext(opinionList.build());
                    response.onCompleted();
                }
            } else {
                OpinionList.Builder opinionList = OpinionList.newBuilder();
                opinionService.getMyOpinions(user).forEach(opinion -> opinionList.addOpinions(
                        buildOpinionResponse(opinion)
                ));
            }
        } catch (Exception e){
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getProfessionalOpinions(GetProfessionalOpinionsRequest request, StreamObserver<OpinionList> response){
        String bearer = AuthInterceptor.getAuthorization();
        String token = bearer.split(" ")[1];
        Professional professional = proDataService.getProfessionalByAccessToken(token);
        if (professional == null){
            Status status = Status.NOT_FOUND.withDescription("Professional not found");
            response.onError(status.asRuntimeException());
            return;
        }

        OpinionList.Builder opinionList = OpinionList.newBuilder();
        opinionService.getOpinionsByProfessional(professional).forEach(opinion -> opinionList.addOpinions(
                buildOpinionResponse(opinion)
        ));

        response.onNext(opinionList.build());
        response.onCompleted();
    }
}
