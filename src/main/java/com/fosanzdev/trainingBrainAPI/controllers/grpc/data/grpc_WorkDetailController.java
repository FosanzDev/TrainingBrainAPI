package com.fosanzdev.trainingBrainAPI.controllers.grpc.data;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.WorkDetail;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IWorkDetailService;
import com.fosanzdev.trainingBrainGrpcInterface.data.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@GrpcService
public class grpc_WorkDetailController extends WorkDetailServiceGrpc.WorkDetailServiceImplBase {

    @Autowired
    private IWorkDetailService workDetailService;

    @Autowired
    private IProDataService proDataService;

    @Autowired
    private grpc_ProDataController grpc_ProDataController;

    @Override
    public void getMyWorkDetails(Empty request, StreamObserver<WorkDetailList> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            WorkDetailList workDetailListBuilder = grpc_ProDataController.getWorkDetails(professional);
            response.onNext(workDetailListBuilder);
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error getting work details");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void addWorkDetail(AddWorkDetailRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            Map<String, Object> body = Map.of(
                    "workTitle", request.getWorkTitleId(),
                    "company", request.getCompany(),
                    "startDate", request.getStartDate(),
                    "endDate", request.getEndDate(),
                    "description", request.getDescription()
            );

            try{
                workDetailService.parseAndAddWorkDetail(professional, body);
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } catch (Exception e){
                Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Something went wrong");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void modifyWorkDetail(ModifyWorkDetailRequest request, StreamObserver<Empty> response){
        try{
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null){
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            Map<String, Object> body = Map.of(
                    "workDetailId", request.getWorkDetailId(),
                    "workTitle", request.getWorkDetail().getWorkTitleId(),
                    "company", request.getWorkDetail().getCompany(),
                    "startDate", request.getWorkDetail().getStartDate(),
                    "endDate", request.getWorkDetail().getEndDate(),
                    "description", request.getWorkDetail().getDescription()
            );

            try{
                workDetailService.parseAndAddWorkDetail(professional, body);
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } catch (Exception e){
                Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e){
            Status status = Status.INTERNAL.withDescription("Something went wrong");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void removeWorkDetail(RemoveWorkDetailRequest request, StreamObserver<Empty> response){
        try{
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null){
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            try{
                boolean success = workDetailService.removeWorkDetail(professional, request.getWorkDetailId());

                if (!success){
                    Status status = Status.INVALID_ARGUMENT.withDescription("Work detail not found");
                    response.onError(status.asRuntimeException());
                    return;
                }

            } catch (Exception e){
                Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
                response.onError(status.asRuntimeException());
            }

            response.onNext(Empty.newBuilder().build());
            response.onCompleted();
        } catch (Exception e){
            Status status = Status.INTERNAL.withDescription("Something went wrong");
            response.onError(status.asRuntimeException());
        }
    }
}
