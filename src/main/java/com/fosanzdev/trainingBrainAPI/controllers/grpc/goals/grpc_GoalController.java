package com.fosanzdev.trainingBrainAPI.controllers.grpc.goals;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.data.User;
import com.fosanzdev.trainingBrainAPI.models.goals.Goal;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.goals.IGoalService;
import com.fosanzdev.trainingBrainGrpcInterface.goals.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

@GrpcService
public class grpc_GoalController extends GoalsServiceGrpc.GoalsServiceImplBase {

    @Autowired
    private IGoalService goalService;

    @Autowired
    private IUserDataService userDataService;

    @Override
    public void addGoal(AddGoalRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            Goal goal = new Goal();
            goal.setTitle(request.getTitle());
            goal.setDescription(request.getDescription());
            goal.setHoursBetween(request.getHoursBetween());
            goal.setRepetitions(request.getRepetitions());
            goal.setStartDateTime(Instant.parse(request.getStartDateTime()));

            if (goalService.addGoal(goal, user)) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.INVALID_ARGUMENT.withDescription("Error adding goal");
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error adding goal");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void markAsDone(GoalByIdRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            Goal goal = goalService.getGoal(user, request.getGoalId());
            if (goal == null) {
                Status status = Status.NOT_FOUND.withDescription("Goal not found");
                response.onError(status.asRuntimeException());
                return;
            }

            if (goalService.markGoalAsDone(goal)) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.ALREADY_EXISTS.withDescription("Goal already completed or done today");
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error marking goal as done");
            response.onError(status.asRuntimeException());
        }
    }

    public GoalResponse buildGoal(Goal goal) {
        GoalResponse.Builder goalResponseBuilder = GoalResponse.newBuilder();
        goalResponseBuilder.setGoalId(goal.getId());
        goalResponseBuilder.setTitle(goal.getTitle() == null ? "" : goal.getTitle());
        goalResponseBuilder.setDescription(goal.getDescription() == null ? "" : goal.getDescription());
        goalResponseBuilder.setCompleted(goal.isCompleted());
        goalResponseBuilder.setHoursBetween(goal.getHoursBetween());
        goalResponseBuilder.setRepetitions(goal.getRepetitions());
        goalResponseBuilder.setStartDateTime(goal.getStartDateTime().toString());
        goalResponseBuilder.setPendingToday(goalService.pendingToday(goal));
        return goalResponseBuilder.build();
    }

    @Override
    public void getTodayGoals(Empty request, StreamObserver<GoalList> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            GoalList.Builder goalListBuilder = GoalList.newBuilder();
            List<Goal> goals = goalService.getAllGoals(user);
            if (goals == null || goals.isEmpty()) {
                response.onNext(goalListBuilder.build());
                response.onCompleted();
                return;
            }

            for (Goal goal : goals) {
                if (goalService.pendingToday(goal)) goalListBuilder.addGoals(buildGoal(goal));
            }

            response.onNext(goalListBuilder.build());
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error getting today goals");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getGoalById(GoalByIdRequest request, StreamObserver<GoalResponse> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            Goal goal = goalService.getGoal(user, request.getGoalId());
            if (goal == null) {
                Status status = Status.NOT_FOUND.withDescription("Goal not found");
                response.onError(status.asRuntimeException());
                return;
            }

            response.onNext(buildGoal(goal));
            response.onCompleted();

        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error getting goal");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getGoals(Empty request, StreamObserver<GoalList> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            GoalList.Builder goalListBuilder = GoalList.newBuilder();
            List<Goal> goals = goalService.getAllGoals(user);
            if (goals == null || goals.isEmpty()) {
                response.onNext(goalListBuilder.build());
                response.onCompleted();
                return;
            }

            for (Goal goal : goals) {
                goalListBuilder.addGoals(buildGoal(goal));
            }

            response.onNext(goalListBuilder.build());
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error getting goals");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void deleteGoal(GoalByIdRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            if (user == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            boolean result = goalService.deleteGoal(user, request.getGoalId());
            if (!result) {
                Status status = Status.INVALID_ARGUMENT.withDescription("Goal not found");
                response.onError(status.asRuntimeException());
                return;
            }

            response.onNext(Empty.newBuilder().build());
            response.onCompleted();
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error deleting goal");
            response.onError(status.asRuntimeException());
        }
    }
}
