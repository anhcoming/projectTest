package com.viettel.hstd.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.hstd.core.config.Message;
import com.viettel.hstd.core.dto.SearchDTO;
import com.viettel.hstd.core.utils.MapUtils;
import com.viettel.hstd.dto.hstd.EmployeeInterviewSessionDTO.*;
import com.viettel.hstd.dto.hstd.InterviewSessionCvDTO;
import com.viettel.hstd.dto.vps.EmployeeVhrDTO;
import com.viettel.hstd.entity.hstd.EmployeeInterviewSessionEntity;
import com.viettel.hstd.entity.hstd.InterviewSessionCvEntity;
import com.viettel.hstd.entity.hstd.InterviewSessionEntity;
import com.viettel.hstd.exception.NotFoundException;
import com.viettel.hstd.repository.hstd.EmployeeInterviewSessionRepository;
import com.viettel.hstd.repository.hstd.InterviewSessionRepository;
import com.viettel.hstd.service.inf.EmployeeInterviewSessionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeInterviewSessionImp extends BaseService implements EmployeeInterviewSessionService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    Message message;

    @Autowired
    EmployeeInterviewSessionRepository employeeInterviewSessionRepository;

    @Autowired
    private InterviewSessionRepository interviewSessionRepository;

    @Autowired
    MapUtils mapUtils;

    @Override
    public Page<EmployeeInterviewSessionResponse> findPage(SearchDTO searchDTO) {
        return EmployeeInterviewSessionService.super.findPage(searchDTO);
    }

    @Override
    public EmployeeInterviewSessionResponse findOneById(Long id) {
        EmployeeInterviewSessionEntity entity = employeeInterviewSessionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        return objectMapper.convertValue(entity, EmployeeInterviewSessionResponse.class);
    }

    @Override
    public Boolean delete(Long id) {
        if (!employeeInterviewSessionRepository.existsById(id))
            throw new NotFoundException(message.getMessage("message.not_found"));
        employeeInterviewSessionRepository.softDelete(id);
        return true;
    }

    @Override
    public EmployeeInterviewSessionResponse create(EmployeeInterviewSessionRequest request) {
        EmployeeInterviewSessionEntity entity = objectMapper.convertValue(request, EmployeeInterviewSessionEntity.class);
        entity = employeeInterviewSessionRepository.save(entity);
        return objectMapper.convertValue(entity, EmployeeInterviewSessionResponse.class);
    }

    @Override
    public EmployeeInterviewSessionResponse update(Long id, EmployeeInterviewSessionRequest request) {
        EmployeeInterviewSessionEntity entity = employeeInterviewSessionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        EmployeeInterviewSessionEntity newE = objectMapper.convertValue(request, EmployeeInterviewSessionEntity.class);
        mapUtils.customMap(newE, entity);
        InterviewSessionEntity interview = interviewSessionRepository.findById(request.interviewSessionId).get();
        if (interview != null) {
            entity.setInterviewSessionEntity(interview);
            entity = employeeInterviewSessionRepository.save(entity);
        }
        return objectMapper.convertValue(entity, EmployeeInterviewSessionResponse.class);
    }

    /**
     * Them hoac cap nhat list nhan vien cho dot phong van
     *
     * @param interviewId ma dot phong van
     * @param lstEmp      danh sach nhan vien cua dot phong van
     * @return
     */
    public Boolean addOrUpdateListEmployee(Long interviewId, List<EmployeeInterviewSessionRequest> lstEmp) {
        InterviewSessionEntity entity = interviewSessionRepository
                .findById(interviewId)
                .orElseThrow(() -> new NotFoundException(message.getMessage("message.not_found")));
        List<EmployeeInterviewSessionEntity> lstEmployee = (
                entity.getEmployeeInterviewSessionEntities().stream().filter(x ->
                        lstEmp.stream().anyMatch(y -> y.employeeId == x.getEmployeeId()))
                        .collect(Collectors.toList()));
        if (lstEmp.size() > 0) {
            int size = lstEmployee.size();
            for (EmployeeInterviewSessionRequest emp : lstEmp) {
                if (emp.employeeId != null && emp.employeeId > 0) {
                    if (!lstEmployee
                            .stream().anyMatch(x -> x.getEmployeeId() == emp.employeeId)
                            || size == 0) {
                        EmployeeInterviewSessionEntity empTemp = new EmployeeInterviewSessionEntity();
                        empTemp.setDelFlag(false);
                        empTemp.setIsActive(true);
                        empTemp.setEmployeeId(emp.employeeId);
                        empTemp.setInterviewSessionEntity(entity);
                        lstEmployee.add(empTemp);
                    }
                }
            }
        } else {
            entity.getEmployeeInterviewSessionEntities().clear();
        }
        entity.getEmployeeInterviewSessionEntities().clear();
        entity.getEmployeeInterviewSessionEntities().addAll(lstEmployee);

        entity = interviewSessionRepository.save(entity);
        return true;
    }

    public List<EmployeeInterviewSessionResponse> findByInterviewSessionId(Long id) {
        ArrayList<EmployeeInterviewSessionEntity> list = employeeInterviewSessionRepository.findByInterviewSessionIdIn(id);
        return list.stream().map(obj ->
                this.objectMapper.convertValue(obj, EmployeeInterviewSessionResponse.class)
        ).collect(Collectors.toList());
    }
}
