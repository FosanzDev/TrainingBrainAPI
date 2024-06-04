package com.fosanzdev.trainingBrainAPI.controllers.grpc.appointments;

import com.fosanzdev.trainingBrainAPI.controllers.grpc.auth.AuthInterceptor;
import com.fosanzdev.trainingBrainAPI.models.appointments.Appointment;
import com.fosanzdev.trainingBrainAPI.models.appointments.Diagnosis;
import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.User;
import com.fosanzdev.trainingBrainAPI.services.appointments.AppointmentException;
import com.fosanzdev.trainingBrainAPI.services.interfaces.appointments.IAppointmentsService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IProDataService;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IUserDataService;
import com.fosanzdev.trainingBrainGrpcInterface.appointments.*;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

@GrpcService
public class grpc_AppointmentController extends AppointmentServiceGrpc.AppointmentServiceImplBase{

    @Autowired
    private IUserDataService userDataService;

    @Autowired
    private IProDataService proDataService;

    @Autowired
    private IAppointmentsService appointmentService;

    @Override
    public void requestAppointment(AppointmentRequest request, StreamObserver<Empty> response){
        try{
            Professional professional = proDataService.getProfessionalById(request.getProfessionalId());
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

            Appointment appointment = new Appointment();
            request.getStartDateTime();
            appointment.setStartDateTime(Instant.parse(request.getStartDateTime()));
            appointment.setStartDateTime(Instant.parse(request.getStartDateTime()));
            appointment.setEndDateTime(Instant.parse(request.getEndDateTime()));
            appointment.setSubmissionTime(Instant.now());
            appointment.setSubmissionNotes(request.getSubmissionNotes());
            appointment.setAppointmentStatus(Appointment.AppointmentStatus.PENDING);
            appointment.setProfessional(professional);
            appointment.setUser(user);

            try{
                appointmentService.bookAppointment(appointment);
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } catch (Exception e){
                Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e){
            Status status = Status.INTERNAL.withDescription("Error requesting appointment");
            response.onError(status.asRuntimeException());
        }
    }

    public DiagnosisObject buildDiagnosisObject(Diagnosis diagnosis){
        DiagnosisObject.Builder diagnosisObjectBuilder = DiagnosisObject.newBuilder();
        diagnosisObjectBuilder.setHeader(diagnosis.getHeader() == null ? "" : diagnosis.getHeader());
        diagnosisObjectBuilder.setShortDescription(diagnosis.getShortDescription() == null ? "" : diagnosis.getShortDescription());
        diagnosisObjectBuilder.setDescription(diagnosis.getDescription() == null ? "" : diagnosis.getDescription());
        diagnosisObjectBuilder.setRecommendation(diagnosis.getRecommendation() == null ? "" : diagnosis.getRecommendation());
        diagnosisObjectBuilder.setTreatment(diagnosis.getTreatment() == null ? "" : diagnosis.getTreatment());
        return diagnosisObjectBuilder.build();
    }

    public AppointmentResponse buildAppointmentResponse(Appointment appointment){
        AppointmentResponse.Builder appointmentResponseBuilder = AppointmentResponse.newBuilder();
        if (appointment.getDiagnosis() != null) {
            DiagnosisObject diagnosisObject = buildDiagnosisObject(appointment.getDiagnosis());
            appointmentResponseBuilder.setDiagnosis(diagnosisObject);
        }

        appointmentResponseBuilder.setAppointmentId(appointment.getId() == null ? "" : appointment.getId());
        appointmentResponseBuilder.setStartDateTime(appointment.getStartDateTime().toString());
        appointmentResponseBuilder.setEndDateTime(appointment.getEndDateTime().toString());
        appointmentResponseBuilder.setSubmissionNotes(appointment.getSubmissionNotes() == null ? "" : appointment.getSubmissionNotes());
        appointmentResponseBuilder.setCancellationNotes(appointment.getCancellationReason() == null ? "" : appointment.getCancellationReason());
        appointmentResponseBuilder.setConfirmationNotes(appointment.getConfirmationNotes() == null ? "" : appointment.getConfirmationNotes());
        appointmentResponseBuilder.setProfessionalId(appointment.getProfessional().getId() == null ? "" : appointment.getProfessional().getId());
        appointmentResponseBuilder.setUserId(appointment.getUser().getId() == null ? "" : appointment.getUser().getId());
        appointmentResponseBuilder.setStatus(appointment.getAppointmentStatus().toString() == null ? "" : appointment.getAppointmentStatus().toString());

        return appointmentResponseBuilder.build();
    }

    @Override
    public void retrieveAppointments(AppointmentRequestByStatus request, StreamObserver<AppointmentList> response){
        try{
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            Professional professional = proDataService.getProfessionalByAccessToken(token);

            if (user == null && professional == null){
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            List<Appointment> appointments;

            if (professional != null)
                appointments = appointmentService.getAppointmentsByStatus(professional, request.getStatus());
            else
                appointments = appointmentService.getAppointmentsByStatus(user, request.getStatus());

            if (appointments == null){
                Status status = Status.NOT_FOUND.withDescription("Invalid status: " + request.getStatus());
                response.onError(status.asRuntimeException());
                return;
            }

            AppointmentList.Builder appointmentListBuilder = AppointmentList.newBuilder();
            for (Appointment appointment : appointments) {
                AppointmentResponse appointmentResponseBuilder = buildAppointmentResponse(appointment);
                appointmentListBuilder.addAppointments(appointmentResponseBuilder);
            }

            response.onNext(appointmentListBuilder.build());
            response.onCompleted();
        } catch (Exception e){
            Status status = Status.INTERNAL.withDescription("Error retrieving appointments");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void acceptAppointment(AppointmentId request, StreamObserver<Empty> response){
        try{
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null){
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            appointmentService.acceptAppointment(request.getAppointmentId(), professional, request.getComment());

            response.onNext(Empty.newBuilder().build());
            response.onCompleted();

        } catch (AppointmentException e){
            Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            response.onError(status.asRuntimeException());
        } catch (Exception e){
            Status status = Status.INTERNAL.withDescription("Error accepting appointment");
            response.onError(status.asRuntimeException());
        }

    }

    @Override
    public void getAppointment(AppointmentId request, StreamObserver<AppointmentResponse> response){
        try{
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            User user = userDataService.getUserByAccessToken(token);
            Professional professional = proDataService.getProfessionalByAccessToken(token);

            if (professional == null && user == null){
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            Appointment appointment;
            if (professional != null)
                appointment = appointmentService.getAppointmentById(professional, request.getAppointmentId());
            else
                appointment = appointmentService.getAppointmentById(user, request.getAppointmentId());

            if (appointment == null){
                Status status = Status.NOT_FOUND.withDescription("Appointment not found");
                response.onError(status.asRuntimeException());
                return;
            }

            AppointmentResponse appointmentResponse = buildAppointmentResponse(appointment);
            response.onNext(appointmentResponse);
            response.onCompleted();

        } catch (Exception e){
            Status status = Status.INTERNAL.withDescription("Error getting appointment");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void cancelAppointment(AppointmentId request, StreamObserver<Empty> response){
        try{
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            User user = userDataService.getUserByAccessToken(token);

            if (professional == null && user == null){
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            if (professional != null)
                appointmentService.rejectAppointment(professional, request.getAppointmentId(), request.getComment());
            else
                appointmentService.rejectAppointment(user, request.getAppointmentId(), request.getComment());


            response.onNext(Empty.newBuilder().build());
            response.onCompleted();

        } catch (AppointmentException e){
            Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
            response.onError(status.asRuntimeException());
        } catch (Exception e){
            Status status = Status.INTERNAL.withDescription("Error cancelling appointment");
            response.onError(status.asRuntimeException());
        }
    }

    @Override
    public void completeAppointment(CompleteRequest request, StreamObserver<Empty> response){
        try{
            String bearer = AuthInterceptor.getAuthorization();
            String token = bearer.split(" ")[1];
            Professional professional = proDataService.getProfessionalByAccessToken(token);
            if (professional == null){
                Status status = Status.UNAUTHENTICATED.withDescription("Unauthorized");
                response.onError(status.asRuntimeException());
                return;
            }

            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setHeader(request.getDiagnosis().getHeader());
            diagnosis.setShortDescription(request.getDiagnosis().getShortDescription());
            diagnosis.setDescription(request.getDiagnosis().getDescription());
            diagnosis.setRecommendation(request.getDiagnosis().getRecommendation());
            diagnosis.setTreatment(request.getDiagnosis().getTreatment());

            try{
                appointmentService.markAsCompleted(professional, request.getAppointmentId(), diagnosis);
                response.onNext(Empty.newBuilder().build());
                response.onCompleted();
            } catch (AppointmentException e){
                Status status = Status.INVALID_ARGUMENT.withDescription(e.getMessage());
                response.onError(status.asRuntimeException());
            }
        } catch (Exception e){
            Status status = Status.INTERNAL.withDescription("Error completing appointment");
            response.onError(status.asRuntimeException());
        }
    }
}
