package com.fosanzdev.trainingBrainAPI.controllers.grpc.goals;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.data.User;
import com.fosanzdev.trainingBrainAPI.models.goals.Routine;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.goals.IRoutineService;
import com.fosanzdev.trainingBrainGrpcInterface.goals.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

@GrpcService
public class grpc_RoutineController extends RoutineServiceGrpc.RoutineServiceImplBase {

    @Autowired
    private IRoutineService routineService;

    @Autowired
    private IUserDataService userDataService;

    @Override
    public void addRoutine(AddRoutineRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) {
                response.onError(new Exception("Unauthorized"));
                return;
            }

            Routine routine = new Routine();
            routine.setTitle(request.getTitle());
            routine.setDescription(request.getDescription());
            routine.setEvery(request.getEvery());
            routine.setRoutineType(Routine.RoutineType.valueOf(request.getRoutineType()));
            routine.setStartTime(Instant.parse(request.getStartDateTime()));
            routine.setEndTime(Instant.parse(request.getEndDateTime()));
            routine.setSubmissionDate(Instant.now());

            if (routineService.addRoutine(routine, user)) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.INVALID_ARGUMENT.withDescription("Error adding routine");
                response.onError(status.asRuntimeException());

            }
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Routine malformed");
            response.onError(status.asRuntimeException());
        }
    }

    public RoutineResponse buildRoutine(Routine routine) {
        RoutineResponse.Builder routineResponseBuilder = RoutineResponse.newBuilder();
        routineResponseBuilder.setId(routine.getId());
        routineResponseBuilder.setTitle(routine.getTitle());
        routineResponseBuilder.setDescription(routine.getDescription());
        routineResponseBuilder.setEvery(routine.getEvery());
        routineResponseBuilder.setRoutineType(routine.getRoutineType().name());
        routineResponseBuilder.setStartDateTime(routine.getStartTime().toString());
        routineResponseBuilder.setEndDateTime(routine.getEndTime().toString());
        routineResponseBuilder.setSubmissionDate(routine.getSubmissionDate().toString());
        return routineResponseBuilder.build();
    }


    @Override
    public void getTodayRoutineList(Empty request, StreamObserver<RoutineList> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) {
                response.onError(new Exception("Unauthorized"));
                return;
            }

            RoutineList.Builder routineListBuilder = RoutineList.newBuilder();
            List<Routine> routines = routineService.getTodayRoutines(user);
            for (Routine routine : routines) {
                routineListBuilder.addRoutines(buildRoutine(routine));
            }

            response.onNext(routineListBuilder.build());
            response.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error getting routine list");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getRoutineList(Empty request, StreamObserver<RoutineList> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) {
                response.onError(new Exception("Unauthorized"));
                return;
            }

            RoutineList.Builder routineListBuilder = RoutineList.newBuilder();
            List<Routine> routines = routineService.getAllRoutines(user);
            for (Routine routine : routines) {
                routineListBuilder.addRoutines(buildRoutine(routine));
            }

            response.onNext(routineListBuilder.build());
            response.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error getting routine list");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getRoutineById(RoutineByIdRequest request, StreamObserver<RoutineResponse> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) {
                response.onError(new Exception("Unauthorized"));
                return;
            }

            Routine routine = routineService.getRoutine(user, request.getRoutineId());
            if (routine == null) {
                Status status = Status.NOT_FOUND.withDescription("Routine not found");
                response.onError(status.asRuntimeException());
                return;
            }

            response.onNext(buildRoutine(routine));
            response.onCompleted();

        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error getting routine");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void deleteRoutine(RoutineByIdRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) {
                response.onError(new Exception("Unauthorized"));
                return;
            }

            boolean result = routineService.deleteRoutine(user, request.getRoutineId());
            if (result) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.INVALID_ARGUMENT.withDescription("Error deleting routine");
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error deleting routine");
            response.onError(status.asRuntimeException());
        }

    }
}
