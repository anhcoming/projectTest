//package com.viettel.hstd.core.config;
//
////import org.quartz.Scheduler;
////import org.quartz.SchedulerException;
////import org.quartz.SchedulerFactory;
////import org.quartz.impl.StdSchedulerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class SchedulerConfig
//{
//    private DataSource dataSource;
//
//    private PlatformTransactionManager transactionManager;
//
//    @Bean
//    public Scheduler scheduler() throws SchedulerException {
//        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
//        Scheduler scheduler = schedulerFactory.getScheduler();
//        return scheduler;
//    }
//
//    @Autowired
//    public SchedulerConfig(@Qualifier("hstdDataSource") DataSource dataSource, @Qualifier("hstdTransactionManager") PlatformTransactionManager transactionManager)
//    {
//        this.dataSource = dataSource;
//        this.transactionManager = transactionManager;
//    }
//
//    @Bean
//    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer()
//    {
//        return bean ->
//        {
//            bean.setDataSource(dataSource);
//            bean.setTransactionManager(transactionManager);
//        };
//    }
//}