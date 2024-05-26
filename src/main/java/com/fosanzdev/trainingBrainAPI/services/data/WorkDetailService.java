package com.fosanzdev.trainingBrainAPI.services.data;

import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.WorkDetail;
import com.fosanzdev.trainingBrainAPI.repositories.data.WorkDetailRepository;
import com.fosanzdev.trainingBrainAPI.repositories.data.WorkTitleRepository;
import com.fosanzdev.trainingBrainAPI.services.interfaces.data.IWorkDetailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
public class WorkDetailService implements IWorkDetailService {

    @Autowired
    private WorkTitleRepository workTitleRepository;

    @Autowired
    private WorkDetailRepository workDetailRepository;

    @Override
    public void parseAndAddWorkDetail(Professional professional, Map<String, Object> body) throws Exception {
        try {
            WorkDetail workDetail = parseWorkDetail(body);
            workDetail.setProfessional(professional);
            workDetailRepository.save(workDetail);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public boolean removeWorkDetail(Professional professional, Long workDetailId) {
        try {
            WorkDetail workDetail = workDetailRepository.findById(workDetailId).orElse(null);
            if (workDetail == null)
                return false;

            for (WorkDetail wd : professional.getWorkDetails()) {
                if (Objects.equals(wd.getId(), workDetailId)) {
                    professional.getWorkDetails().remove(wd);
                    workDetailRepository.delete(wd);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public WorkDetail parseWorkDetail(Map<String, Object> body) throws Exception {
        WorkDetail workDetail = new WorkDetail();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        sdf.setLenient(false); // This will ensure strict date parsing (e.g. 2021/13 will throw an exception)

        String startDateStr = (String) body.get("startDate");
        String endDateStr = (String) body.get("endDate");

        // Parse the dates and check if they are well formatted
        Date startDate, endDate;
        try {
            startDate = sdf.parse(startDateStr);
            endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            throw new Exception("Invalid date format");
        }

        // Check if startDate is not greater than endDate
        if (startDate.after(endDate)) {
            throw new Exception("Start date cannot be after end date");
        }

        // Save the dates as strings in the format "yyyy/MM"
        workDetail.setStartDate(startDateStr);
        workDetail.setEndDate(endDateStr);

        try {
            workDetail.setEnterprise((String) body.get("enterprise"));
            workDetail.setDescription((String) body.get("description"));
        } catch (Exception e) {
            throw new Exception("Invalid data");
        }

        try {
            workTitleRepository.findById((Long) body.get("workTitle")).ifPresent(workDetail::setWorkTitle);
        } catch (Exception e) {
            throw new Exception("Invalid work title");
        }

        return workDetail;
    }

    @Transactional
    @Override
    public void parseAndEditWorkDetail(Professional professional, Map<String, Object> body, Long workDetailId) throws Exception {
        try {
            workDetailRepository.findById(workDetailId).orElseThrow(() -> new Exception("Work detail not found"));
            WorkDetail workDetail = parseWorkDetail(body);
            workDetail.setId(workDetailId);
            workDetail.setProfessional(professional);
            workDetailRepository.save(workDetail);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
