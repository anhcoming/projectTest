//package com.viettel.hstd.schedule;
//
//import com.viettel.hstd.service.inf.CvService;
//import com.viettel.hstd.service.inf.InterviewSessionService;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.quartz.JobDetailFactoryBean;
//import org.springframework.stereotype.Component;
//
//@Component
//public class UpdateInterviewStateJob implements Job {
//
//    @Autowired
//    private InterviewSessionService interviewSessionService;
//
//    public void execute(JobExecutionContext context) {
//        Long interviewSessionId = (Long) context.getJobDetail().getJobDataMap().get("interviewSessionId");
//
//        interviewSessionService.updateCvThatInterviewed(interviewSessionId);
//    }
//}