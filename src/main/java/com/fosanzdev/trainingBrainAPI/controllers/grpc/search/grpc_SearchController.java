package com.fosanzdev.trainingBrainAPI.controllers.grpc.search;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.data.grpc_ProDataController;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.repositories.data.ProfessionalRepository;
import com.fosanzdev.trainingBrainAPI.repositories.data.ProfessionalSpecification;
import com.fosanzdev.trainingBrainGrpcInterface.data.WorkDetailList;
import com.fosanzdev.trainingBrainGrpcInterface.posts.ByBranch;
import com.fosanzdev.trainingBrainGrpcInterface.posts.ByWorkTitle;
import com.fosanzdev.trainingBrainGrpcInterface.posts.ProDataList;
import com.fosanzdev.trainingBrainGrpcInterface.posts.SearchServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@GrpcService
public class grpc_SearchController extends SearchServiceGrpc.SearchServiceImplBase {

    @Autowired
    private ProfessionalRepository professionalRepository;
    @Autowired
    private grpc_ProDataController grpc_ProDataController;

    @Override
    public void searchByWorkTitle(ByWorkTitle request, StreamObserver<ProDataList> response) {
        try {
            int size = request.getPaginaton().getPageSize();
            int page = request.getPaginaton().getPage();

            if (size <= 0 || size > 20) size = 20;
            if (page < 0) page = 0;

            Pageable pageable = PageRequest.ofSize(size).withPage(page);

            String name = null;
            List<Long> hasSkills = null;
            Integer rating = null;

            if (request.getSubFilters() != null) {
                name = request.getSubFilters().getName();
                hasSkills = request.getSubFilters().getHasSkills().getSkillIdList();
                rating = request.getSubFilters().getRating();
            }

            Page<Professional> professionals = professionalRepository.findAll(
                    new ProfessionalSpecification("worktitle", request.getWorkTitle(), name, hasSkills, rating), pageable
            );

            ProDataList.Builder proDataListBuilder = ProDataList.newBuilder();
            for (Professional professional : professionals) {
                WorkDetailList workDetailList = grpc_ProDataController.getWorkDetails(professional);
                proDataListBuilder.addProfessional(grpc_ProDataController.generateProDataResponse(professional, workDetailList));
            }

            response.onNext(proDataListBuilder.build());
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error getting professionals");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void searchByBranch(ByBranch request, StreamObserver<ProDataList> response) {
        try {
            int size = request.getPagination().getPageSize();
            int page = request.getPagination().getPage();

            if (size <= 0 || size > 20) size = 20;
            if (page < 0) page = 0;

            Pageable pageable = PageRequest.of(page, size);

            String name = null;
            List<Long> hasSkills = null;
            Integer rating = null;

            if (request.getSubFilters() != null) {
                name = request.getSubFilters().getName();
                hasSkills = request.getSubFilters().getHasSkills().getSkillIdList();
                rating = request.getSubFilters().getRating();
            }

            Page<Professional> professionals = professionalRepository.findAll(
                    new ProfessionalSpecification("branch", request.getBranch(), name, hasSkills, rating), pageable
            );

            ProDataList.Builder proDataListBuilder = ProDataList.newBuilder();
            for (Professional professional : professionals) {
                WorkDetailList workDetailList = grpc_ProDataController.getWorkDetails(professional);
                proDataListBuilder.addProfessional(grpc_ProDataController.generateProDataResponse(professional, workDetailList));
            }

            response.onNext(proDataListBuilder.build());
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error getting professionals");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void searchAll(Empty request, StreamObserver<ProDataList> response) {
        try {
            int size = 20;
            int page = 0;

            Pageable pageable = PageRequest.of(page, size);

            Page<Professional> professionals = professionalRepository.findAll(pageable);

            ProDataList.Builder proDataListBuilder = ProDataList.newBuilder();
            for (Professional professional : professionals) {
                WorkDetailList workDetailList = grpc_ProDataController.getWorkDetails(professional);
                proDataListBuilder.addProfessional(grpc_ProDataController.generateProDataResponse(professional, workDetailList));
            }

            response.onNext(proDataListBuilder.build());
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error getting professionals");
            response.onError(status.asRuntimeException());
        }
    }
}
