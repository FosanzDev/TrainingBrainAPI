package com.fosanzdev.trainingBrainAPI.services.appointments;

public class AppointmentException extends Exception {
    public AppointmentException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
