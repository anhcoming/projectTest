package com.viettel.hstd.core.config;//package com.viettel.iim.core.config;
//
//
//import org.quartz.Scheduler;
//import org.quartz.SchedulerContext;
//import org.quartz.SchedulerFactory;
//import org.quartz.impl.StdSchedulerFactory;
//import org.quartz.spi.JobFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.config.PropertiesFactoryBean;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//
//import java.io.IOException;
//import java.util.Properties;
//
//@Configuration
//
//public class QuartzConfiguration {
//    @Autowired
//    ApplicationContext applicationContext;
//
//    @Bean
//    public Scheduler scheduler() throws IOException {
//        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
//
//        jobFactory.setApplicationContext(applicationContext);
//
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        factory.setOverwriteExistingJobs(true);
//        factory.setSchedulerFactory( new StdSchedulerFactory());
//        factory.setAutoStartup(true);
//        factory.setJobFactory(jobFactory);
//        factory.setQuartzProperties(quartzProperties());
//        factory.
//
//        return factory.getObject();
//
//    }
//
//    @Bean
//    public Properties quartzProperties() throws IOException {
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.afterPropertiesSet();
//        return propertiesFactoryBean.getObject();
//    }
//}