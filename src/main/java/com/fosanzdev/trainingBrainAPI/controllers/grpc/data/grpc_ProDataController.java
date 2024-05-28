package com.fosanzdev.trainingBrainAPI.controllers.grpc.data;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.repositories.data.BranchRepository;
import com.fosanzdev.trainingBrainAPI.repositories.data.WorkTitleRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainGrpcInterface.data.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class grpc_ProDataController extends ProDataServiceGrpc.ProDataServiceImplBase {

    @Autowired
    private IProDataService proDataService;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private WorkTitleRepository workTitleRepository;

    @Override
    public void getBranchList(Empty request, StreamObserver<BranchListResponse> responseObserver) {
        try {
            BranchListResponse.Builder response = BranchListResponse.newBuilder();
            branchRepository.findAll().forEach(branch -> response.addBranchList(Branch.newBuilder()
                    .setId(branch.getId())
                    .setName(branch.getName())
                    .build()));
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error getting branch list");
            responseObserver.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getWorkTitleList(Empty request, StreamObserver<WorkTitleListResponse> response) {
        try {
            WorkTitleListResponse.Builder responseBuilder = WorkTitleListResponse.newBuilder();
            workTitleRepository.findAll().forEach(workTitle -> responseBuilder.addWorkTitleList(WorkTitle.newBuilder()
                    .setId(workTitle.getId())
                    .setName(workTitle.getTitle())
                    .setBranch(Branch.newBuilder()
                            .setId(workTitle.getBranch().getId())
                            .setName(workTitle.getBranch().getName())
                            .build())
                    .build()));

            response.onNext(responseBuilder.build());
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error getting work title list");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getWorkTitleByBranch(GetWorkTitleByBranchRequest request, StreamObserver<WorkTitleListResponse> response) {
        try {
            WorkTitleListResponse.Builder responseBuilder = WorkTitleListResponse.newBuilder();
            workTitleRepository.findByBranch(request.getBranchId()).forEach(workTitle -> responseBuilder.addWorkTitleList(WorkTitle.newBuilder()
                    .setId(workTitle.getId())
                    .setName(workTitle.getTitle())
                    .setBranch(Branch.newBuilder()
                            .setId(workTitle.getBranch().getId())
                            .setName(workTitle.getBranch().getName())
                            .build())
                    .build()));

            response.onNext(responseBuilder.build());
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error getting work title list");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void setWorkTitle(SetWorkTitleRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
            }

            if (proDataService.setWorkTitle(professional, request.getWorktTitleId())) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.NOT_FOUND.withDescription("Work title not found");
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            response.onError(status.asRuntimeException());
        }
    }

    public WorkDetailList getWorkDetails(Professional professional) {
        WorkDetailList.Builder workDetailListBuilder = WorkDetailList.newBuilder();
        professional.getWorkDetails().forEach(workDetail -> workDetailListBuilder.addWorkDetails(
                WorkDetail.newBuilder()
                        .setId(workDetail.getId())
                        .setWorkTitle(WorkTitle.newBuilder()
                                .setId(workDetail.getWorkTitle().getId())
                                .setName(workDetail.getWorkTitle().getTitle())
                                .setBranch(Branch.newBuilder()
                                        .setId(workDetail.getWorkTitle().getBranch().getId())
                                        .setName(workDetail.getWorkTitle().getBranch().getName())
                                        .build())
                                .build())
                        .setCompany(workDetail.getEnterprise())
                        .setStartDate(workDetail.getStartDate())
                        .setEndDate(workDetail.getEndDate())
                        .setDescription(workDetail.getDescription())
                        .build()));
        return workDetailListBuilder.build();
    }

    public ProDataResponse generateProDataResponse(Professional professional, WorkDetailList workDetailList) {
        return ProDataResponse.newBuilder()
                .setId(professional.getId())
                .setName(professional.getAccount().getName())
                .setPublicBio(professional.getPublicBio())
                .setWorkTitle(WorkTitle.newBuilder()
                        .setId(professional.getWorkTitle().getId())
                        .setName(professional.getWorkTitle().getTitle())
                        .setBranch(Branch.newBuilder()
                                .setId(professional.getWorkTitle().getBranch().getId())
                                .setName(professional.getWorkTitle().getBranch().getName())
                                .build())
                        .build())
                .setWorkDetails(workDetailList)
                .build();
    }

    @Override
    public void getMyData(Empty request, StreamObserver<ProDataResponse> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            WorkDetailList workDetailList = getWorkDetails(professional);
            ProDataResponse proDataResponse = generateProDataResponse(professional, workDetailList);

            response.onNext(proDataResponse);
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getDataById(GetDataByIdRequest request, StreamObserver<ProDataResponse> response) {
        try {
            Professional professional = proDataService.getProfessionalById(request.getId());
            if (professional == null) {
                Status status = Status.NOT_FOUND.withDescription("Professional not found");
                response.onError(status.asRuntimeException());
                return;
            }

            WorkDetailList workDetailList = getWorkDetails(professional);
            ProDataResponse proDataResponse = generateProDataResponse(professional, workDetailList);

            response.onNext(proDataResponse);
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void setMyData(SetMyDataRequest request, StreamObserver<Empty> response){
            try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professionalToUpdate = proDataService.getProfessionalByAccessToken(token);
            if (professionalToUpdate == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            Professional professional = new Professional();
            professional.setPublicBio(request.getPublicBio());

            try{
                proDataService.updateProfessional(professionalToUpdate, professional);
            } catch (Exception e){
                Status status = Status.INTERNAL.withDescription("Error updating professional");
                response.onError(status.asRuntimeException());
                return;
            }

            response.onNext(Empty.newBuilder().build());
            response.onCompleted();

        } catch (Exception e){
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            response.onError(status.asRuntimeException());
        } }

}
