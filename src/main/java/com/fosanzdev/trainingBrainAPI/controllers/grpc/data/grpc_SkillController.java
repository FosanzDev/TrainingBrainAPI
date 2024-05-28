package com.fosanzdev.trainingBrainAPI.controllers.grpc.data;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.ProfessionalSkill;
import com.fosanzdev.trainingBrainAPI.models.data.Skill;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.ISkillService;
import com.fosanzdev.trainingBrainGrpcInterface.data.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class grpc_SkillController extends SkillServiceGrpc.SkillServiceImplBase {

    @Autowired
    private ISkillService skillService;

    @Autowired
    private IProDataService proDataService;

    @Override
    public void getAllSkills(Empty request, StreamObserver<SkillList> response) {
        List<Skill> skillList = skillService.getAll();
        SkillList.Builder skillListBuilder = SkillList.newBuilder();
        for (Skill skill : skillList) {
            com.fosanzdev.trainingBrainGrpcInterface.data.Skill.Builder skillBuilder = com.fosanzdev.trainingBrainGrpcInterface.data.Skill.newBuilder();
            skillBuilder.setId(skill.getId());
            skillBuilder.setName(skill.getName());
            skillListBuilder.addSkills(skillBuilder);
        }

        response.onNext(skillListBuilder.build());
        response.onCompleted();
    }

    @Override
    public void getMySkills(Empty request, StreamObserver<ProfessionalSkillList> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            List<ProfessionalSkill> professionalSkillList = proDataService.getProfessionalByAccessToken(token).getProfessionalSkills();
            ProfessionalSkillList.Builder professionalSkillListBuilder = ProfessionalSkillList.newBuilder();
            for (ProfessionalSkill professionalSkill : professionalSkillList) {
                com.fosanzdev.trainingBrainGrpcInterface.data.ProfessionalSkill.Builder professionalSkillBuilder = com.fosanzdev.trainingBrainGrpcInterface.data.ProfessionalSkill.newBuilder();
                professionalSkillBuilder.setId(professionalSkill.getId());
                professionalSkillBuilder.setName(professionalSkill.getSkill().getName());
                professionalSkillBuilder.setDescription(professionalSkill.getSkill().getDescription());
                professionalSkillBuilder.setLevel(professionalSkill.getLevel());
                professionalSkillListBuilder.addProfessionalSkills(professionalSkillBuilder);
            }

            response.onNext(professionalSkillListBuilder.build());
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void addSkill(SkillRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            Skill skill = skillService.getSkillById(request.getId());
            if (skill == null) {
                Status status = Status.INVALID_ARGUMENT.withDescription("Skill not found");
                response.onError(status.asRuntimeException());
                return;
            }

            if (request.getLevel() <= 0 || request.getLevel() > 10) {
                Status status = Status.INVALID_ARGUMENT.withDescription("Invalid level");
                response.onError(status.asRuntimeException());
                return;
            }

            skillService.addNewSkill(professional, skill, request.getLevel());
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void modifySkill(SkillRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            Skill skill = skillService.getSkillById(request.getId());
            if (skill == null) {
                Status status = Status.INVALID_ARGUMENT.withDescription("Skill not found");
                response.onError(status.asRuntimeException());
                return;
            }

            if (request.getLevel() <= 0 || request.getLevel() > 10) {
                Status status = Status.INVALID_ARGUMENT.withDescription("Invalid level");
                response.onError(status.asRuntimeException());
                return;
            }

            boolean success = skillService.updateSkill(professional, skill, request.getLevel());
            if (!success) {
                Status status = Status.INVALID_ARGUMENT.withDescription("Skill not found");
                response.onError(status.asRuntimeException());
            } else {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            }
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void deleteSkill(DeleteSkillRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            Skill skill = skillService.getSkillById(request.getId());
            if (skill == null) {
                Status status = Status.INVALID_ARGUMENT.withDescription("Skill not found");
                response.onError(status.asRuntimeException());
                return;
            }

            boolean success = skillService.deleteSkill(professional, skill);

            if (success) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.INVALID_ARGUMENT.withDescription("Skill not found");
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Invalid request parameters");
            response.onError(status.asRuntimeException());
        }
    }
}
