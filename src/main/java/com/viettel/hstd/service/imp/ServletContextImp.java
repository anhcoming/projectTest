//package com.viettel.hstd.service.imp;
//
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//
//public class ServletContextImp implements ServletContextListener {
//    @Autowired
//    Scheduler scheduler;
//
//    @Override
//    public void contextDestroyed(ServletContextEvent sce) {
//        try {
//            scheduler.shutdown(true);
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//    }
//}
