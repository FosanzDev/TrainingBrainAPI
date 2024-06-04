package com.fosanzdev.trainingBrainAPI.models.appointments;

import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "professional_schedules")
public class ProfessionalSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private int dayOfWeek; // 1 = Monday according to ISO-8601
    private Instant startHour;
    private Instant endHour;
    private int intervalMinutes; // Interval between appointments in minutes

    @ManyToOne()
    @JoinColumn(name = "fk_professional", referencedColumnName = "id")
    private Professional professional;

    public static List<ProfessionalSchedule> fromNamedDayJsonSchedule(Map<String, Object> schedule) {
        List<ProfessionalSchedule> professionalSchedules = new ArrayList<>();
        List<String> days = List.of("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");
        int intervalMinutes = schedule.get("intervalMinutes") != null ? (int) schedule.get("intervalMinutes") : 30;

        try {
            for (String day : schedule.keySet()) {
                if (days.contains(day)) {
                    List<Map<String, Object>> daySchedules = (List<Map<String, Object>>) schedule.get(day);
                    for (Map<String, Object> daySchedule : daySchedules) {
                        ProfessionalSchedule professionalSchedule = new ProfessionalSchedule();
                        professionalSchedule.setDayOfWeek(days.indexOf(day) + 1);
                        if (
                                daySchedule.containsKey("startHour") && daySchedule.containsKey("endHour") &&
                                        checkHourFormat((String) daySchedule.get("startHour")) && checkHourFormat((String) daySchedule.get("endHour"))
                        ) {
                            professionalSchedule.setStartHour(Instant.parse("1970-01-01T" + daySchedule.get("startHour") + ":00Z"));
                            professionalSchedule.setEndHour(Instant.parse("1970-01-01T" + daySchedule.get("endHour") + ":00Z"));
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

    public static Map<String, Object> toNamedDayJsonSchedule(List<ProfessionalSchedule> professionalSchedules) {
        Map<Integer, List<ProfessionalSchedule>> schedulesByDay = professionalSchedules.stream()
                .collect(Collectors.groupingBy(ProfessionalSchedule::getDayOfWeek));

        Map<String, Object> schedule = schedulesByDay.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> List.of("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday").get(entry.getKey() - 1),
                        entry -> entry.getValue().stream()
                                .map(professionalSchedule -> Map.of(
                                        "startHour", professionalSchedule.getStartHour().toString().substring(11, 16),
                                        "endHour", professionalSchedule.getEndHour().toString().substring(11, 16)
                                ))
                                .collect(Collectors.toList())
                ));

        schedule.put("intervalMinutes", professionalSchedules.get(0).getIntervalMinutes());
        return schedule;
    }

    public static boolean checkHourFormat(String hour) {
        return hour.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    public static boolean checkForConflicts(List<ProfessionalSchedule> professionalSchedules) {
        Map<Integer, List<ProfessionalSchedule>> schedulesByDay = professionalSchedules.stream()
                .collect(Collectors.groupingBy(ProfessionalSchedule::getDayOfWeek));

        for (List<ProfessionalSchedule> schedules : schedulesByDay.values()) {
            for (int i = 0; i < schedules.size(); i++) {
                for (int j = i + 1; j < schedules.size(); j++) {
                    ProfessionalSchedule schedule1 = schedules.get(i);
                    ProfessionalSchedule schedule2 = schedules.get(j);

                    // Check for repeated entries
                    if (schedule1.equals(schedule2)) {
                        return false;
                    }

                    // Check if endHour is greater than startHour
                    if (schedule1.getStartHour().isAfter(schedule1.getEndHour()) || schedule2.getStartHour().isAfter(schedule2.getEndHour())) {
                        return false;
                    }

                    // Check if next startHour ends with past endHour
                    if (schedule1.getStartHour().isBefore(schedule2.getEndHour()) && schedule1.getEndHour().isAfter(schedule2.getStartHour()) ||
                            schedule1.getStartHour().equals(schedule2.getEndHour()) || schedule1.getEndHour().equals(schedule2.getStartHour())) {
                        return false;
                    }

                    // Check if time between startHour and endHour is lower than interval
                    long duration1 = Duration.between(schedule1.getStartHour(), schedule1.getEndHour()).toMinutes();
                    long duration2 = Duration.between(schedule2.getStartHour(), schedule2.getEndHour()).toMinutes();
                    if (duration1 < schedule1.getIntervalMinutes() || duration2 < schedule2.getIntervalMinutes()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
