package com.viettel.hstd;

import com.viettel.hstd.entity.listener.CVListener;
import com.viettel.hstd.entity.listener.InterviewSessionListener;
import com.viettel.hstd.repository.hstd.CvRepository;
import com.viettel.hstd.repository.vps.EmployeeVhrRepository;
import com.viettel.hstd.repository.vps.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@ConfigurationPropertiesScan
public class HstdApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(HstdApplication.class, args);
    }

//    @Autowired
//    PositionRepository positionRepository;
//
//    @Autowired
//    EmployeeVhrRepository employeeVhrRepository;
//
//    @Autowired
//    CvRepository cvRepository;
//
//    @PersistenceContext
//    EntityManager entityManager;
//
//    @EventListener(ApplicationReadyEvent.class)
//    private void initListener() {
//        InterviewSessionListener interviewSessionListener = new InterviewSessionListener();
//        interviewSessionListener.init(positionRepository, employeeVhrRepository);
//
//        CVListener cvListener = new CVListener();
//        cvListener.init(cvRepository, entityManager);
//    }
}
