package com.fosanzdev.trainingBrainAPI.controllers.grpc.appointments;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.appointments.ProfessionalHoliday;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IProHolidaysService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainGrpcInterface.appointments.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

@GrpcService
public class grpc_HolidayController extends HolidayServiceGrpc.HolidayServiceImplBase {

    @Autowired
    private IProHolidaysService proHolidaysService;

    @Autowired
    private IProDataService proDataService;

    @Override
    public void addHoliday(NewHolidayRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            ProfessionalHoliday professionalHoliday = new ProfessionalHoliday();
            professionalHoliday.setHolidayType(ProfessionalHoliday.HolidayType.valueOf(request.getHolidayType()));
            professionalHoliday.setDescription(request.getDescription());
            professionalHoliday.setStartDateTime(Instant.parse(request.getStartDateTime()));
            professionalHoliday.setEndDateTime(Instant.parse(request.getEndDateTime()));

            if (proHolidaysService.addHoliday(professional, professionalHoliday)) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.INVALID_ARGUMENT.withDescription("Error adding holiday");
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INVALID_ARGUMENT.withDescription("Error adding holiday");
            response.onError(status.asRuntimeException());
        }

    }

    @Override
    public void removeHoliday(RemoveHolidayRequest request, StreamObserver<Empty> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            if (proHolidaysService.deleteHoliday(professional, request.getId())) {
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } else {
                Status status = Status.INVALID_ARGUMENT.withDescription("Holiday not found");
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error removing holiday");
            response.onError(status.asRuntimeException());
        }
    }

    public HolidayResponse buildHolidayResponse(ProfessionalHoliday holiday, boolean showId) {
        HolidayResponse.Builder builder =  HolidayResponse.newBuilder()
                .setHolidayType(holiday.getHolidayType().toString())
                .setDescription(holiday.getDescription())
                .setStartDateTime(holiday.getStartDateTime().toString())
                .setEndDateTime(holiday.getEndDateTime().toString());

        if (showId) return builder.setId(holiday.getId()).build();

        else return builder.build();
    }

    @Override
    public void getMyHolidays(Empty request, StreamObserver<HolidayListResponse> response) {
        try {
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null) {
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }


            List<ProfessionalHoliday> holidays = proHolidaysService.findByProfessionalId(professional.getId());
            HolidayListResponse.Builder holidayListResponseBuilder = HolidayListResponse.newBuilder();
            for (ProfessionalHoliday holiday : holidays) {
                holidayListResponseBuilder.addHolidays(buildHolidayResponse(holiday, true));
            }

            response.onNext(holidayListResponseBuilder.build());
            response.onCompleted();

        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error getting holidays");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void getHolidayById(GetHolidayByIdRequest request, StreamObserver<HolidayResponse> response) {
        try{
            Professional professional = proDataService.getProfessionalById(request.getId());
            if (professional == null){
                Status status = Status.NOT_FOUND.withDescription("Professional not found");
                response.onError(status.asRuntimeException());
                return;
            }

            List<ProfessionalHoliday> holidays = proHolidaysService.findByProfessionalId(professional.getId());
            HolidayListResponse.Builder holidayListResponseBuilder = HolidayListResponse.newBuilder();
            for (ProfessionalHoliday holiday : holidays) {
                holidayListResponseBuilder.addHolidays(buildHolidayResponse(holiday, false));
            }

        } catch (Exception e) {
            Status status = Status.INTERNAL.withDescription("Error getting holidays");
            response.onError(status.asRuntimeException());
        }
    }
}
