package com.fosanzdev.trainingBrainAPI.services.interfaces.data;

import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.WorkDetail;

import java.util.Map;

public interface IWorkDetailService {
    void parseAndAddWorkDetail(Professional professional, Map<String, Object> body) throws Exception;
    void parseAndEditWorkDetail(Professional professional, Map<String, Object> body, Long workDetailId) throws Exception;
    WorkDetail parseWorkDetail(Map<String, Object> body) throws Exception;
    boolean removeWorkDetail(Professional professional, Long workDetailId);
}
