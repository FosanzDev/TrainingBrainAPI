package com.fosanzdev.trainingBrainAPI.controllers.grpc.appointments;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalHoliday;
import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalSchedule;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IProScheduleService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainGrpcInterface.appointments.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalSchedule.checkForConflicts;
import static com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalSchedule.checkHourFormat;

@GrpcService
public class grpc_ScheduleController extends ScheduleServiceGrpc.ScheduleServiceImplBase {

    @Autowired
    private IProScheduleService proScheduleService;

    @Autowired
    private IProDataService proDataService;

    public List<ProfessionalSchedule> buildSchedule(Schedule request) {
        List<ProfessionalSchedule> professionalSchedules = new ArrayList<>();
        List<String> days = List.of("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");
        int intervalMinutes = request.getIntervalMinutes();

        try {
            for (Day day : request.getScheduleList()) {
                if (days.contains(day.getDay().toLowerCase())) {
                    for (Range range : day.getRangeList()) {
                        ProfessionalSchedule professionalSchedule = new ProfessionalSchedule();
                        professionalSchedule.setDayOfWeek(days.indexOf(day.getDay().toLowerCase()) + 1);
                        if (
                                checkHourFormat(range.getStartHour()) && checkHourFormat(range.getEndHour())
                        ) {
                            professionalSchedule.setStartHour(Instant.parse("1970-01-01T" + range.getStartHour() + ":00Z"));
                            professionalSchedule.setEndHour(Instant.parse("1970-01-01T" + range.getEndHour() + ":00Z"));
                            professionalSchedule.setIntervalMinutes(intervalMinutes);
                            professionalSchedules.add(professionalSchedule);
                        } else {
                            return null;
                        }
                    }
                }
            }
            if (checkForConflicts(professionalSchedules)) return professionalSchedules;
            else return null;
        } catch (Exception e) {
            return null;
        }
    }

    public Schedule toScheduleProto(List<ProfessionalSchedule> professionalSchedules) {
        // Group schedules by day
        Map<Integer, List<ProfessionalSchedule>> schedulesByDay = professionalSchedules.stream()
                .collect(Collectors.groupingBy(ProfessionalSchedule::getDayOfWeek));

        // Initialize Schedule builder
        Schedule.Builder scheduleBuilder = Schedule.newBuilder();
        String id = professionalSchedules.get(0).getProfessional().getId();
        scheduleBuilder.setProfessionalId(id == null ? "" : id);

        // Iterate over each day
        for (Map.Entry<Integer, List<ProfessionalSchedule>> entry : schedulesByDay.entrySet()) {
            // Initialize Day builder
            Day.Builder dayBuilder = Day.newBuilder();
            dayBuilder.setDay(List.of("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday").get(entry.getKey() - 1));

            // Iterate over each schedule in a day
            for (ProfessionalSchedule professionalSchedule : entry.getValue()) {
                // Initialize Range builder
                Range.Builder rangeBuilder = Range.newBuilder();
                rangeBuilder.setStartHour(professionalSchedule.getStartHour().toString().substring(11, 16));
                rangeBuilder.setEndHour(professionalSchedule.getEndHour().toString().substring(11, 16));

                // Add Range to Day
                dayBuilder.addRange(rangeBuilder);
            }

            // Add Day to Schedule
            scheduleBuilder.addSchedule(dayBuilder);
        }

        // Set intervalMinutes
        scheduleBuilder.setIntervalMinutes(professionalSchedules.get(0).getIntervalMinutes());

        // Build and return Schedule
        return scheduleBuilder.build();
    }

    @Override
    public void modifySchedule(Schedule request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            List<ProfessionalSchedule> schedules = buildSchedule(request);
            if (schedules == null) {
                Status status = Status.INVALID_ARGUMENT.withDescription("Invalid schedule");
                response.onError(status.asRuntimeException());
                return;
            }

            if (proScheduleService.changeSchedule(professional, schedules)) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.INVALID_ARGUMENT.withDescription("Error changing schedule");
                response.onError(status.asRuntimeException());
            }

        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Something went wrong");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getScheduleById(ScheduleByIdRequest request, StreamObserver<Schedule> responseObserver) {
        try {
            String professionalId = request.getProfessionalId();
            List<ProfessionalSchedule> schedules = proScheduleService.findByProfessionalId(professionalId);
            if (schedules == null || schedules.isEmpty()) {
                Status status = Status.NOT_FOUND.withDescription("No schedule found");
                responseObserver.onError(status.asRuntimeException());
                return;
            }

            Schedule schedule = toScheduleProto(schedules);
            responseObserver.onNext(schedule);
            responseObserver.onCompleted();

        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error getting schedule");
            responseObserver.onError(status.asRuntimeException());
        }
    }

}
