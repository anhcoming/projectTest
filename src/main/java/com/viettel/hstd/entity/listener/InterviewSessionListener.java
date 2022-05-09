package com.viettel.hstd.entity.listener;

import com.viettel.hstd.entity.hstd.InterviewSessionEntity;
import com.viettel.hstd.entity.vps.EmployeeVhrEntity;
import com.viettel.hstd.entity.vps.PositionEntity;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Configurable
public class InterviewSessionListener {

    static private PositionRepository positionRepository;

    static private EmployeeVhrRepository employeeVhrRepository;

    @Autowired
    public void init(PositionRepository positionRepository, EmployeeVhrRepository employeeVhrRepository) {
        InterviewSessionListener.positionRepository = positionRepository;
        InterviewSessionListener.employeeVhrRepository = employeeVhrRepository;
    }

    @PrePersist
    @PreUpdate
    private void fillPositionEmployee(InterviewSessionEntity interviewSessionEntity) {
        // Fill Position
        Long positionId = interviewSessionEntity.getPositionId();

        PositionEntity positionEntity = positionRepository.findById(positionId).orElse(new PositionEntity());

        interviewSessionEntity.setPositionCode(positionEntity.getPositionCode());
        interviewSessionEntity.setPositionName(positionEntity.getPositionName());

        // Fill Employee
        Long leaderId = interviewSessionEntity.getLeaderId();

        EmployeeVhrEntity employeeEntity = employeeVhrRepository.findById(leaderId).orElse(new EmployeeVhrEntity());

        interviewSessionEntity.setLeaderName(employeeEntity.getFullname());
        interviewSessionEntity.setLeaderEmail(employeeEntity.getEmail());
    }

}