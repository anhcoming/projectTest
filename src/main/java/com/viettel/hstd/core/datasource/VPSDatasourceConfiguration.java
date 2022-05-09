package com.viettel.hstd.core.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.viettel.hstd.repository.vps",
        entityManagerFactoryRef = "vpsEntityManagerFactory",
        transactionManagerRef= "vpsTransactionManager")

public class VPSDatasourceConfiguration {
//    @Bean
//    @ConfigurationProperties("app.datasource.vps")
//    public DataSourceProperties vpsDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    @ConfigurationProperties("app.datasource.vps.configuration")
//    public DataSource vpsDataSource() {
//        return vpsDataSourceProperties().initializeDataSourceBuilder()
//                .type(BasicDataSource.class).build();
//    }
//
//    @Bean(name = "vpsEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean vpsEntityManagerFactory(
//            EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(vpsDataSource())
//                .packages("com.viettel.hstd.entity.vps")
//                .build();
//    }
//
//    @Bean(name = "vpsTransactionManager")
//    public PlatformTransactionManager vpsTransactionManager(EntityManagerFactoryBuilder builder) {
//        JpaTransactionManager tm = new JpaTransactionManager();
//        tm.setEntityManagerFactory(vpsEntityManagerFactory(builder).getObject());
//        tm.setDataSource(vpsDataSource());
//        return tm;
//    }

    @Bean
    @ConfigurationProperties("app.datasource.vps")
    public HikariConfig vpsHikariConfig() {
        HikariConfig config = new HikariConfig();
        return new HikariConfig();
    }

    @Bean
    public DataSourceProperties vpsDataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        HikariConfig hikariConfig = new HikariConfig();
        properties.setDriverClassName(hikariConfig.getDriverClassName());
        properties.setUrl(hikariConfig.getJdbcUrl());
        properties.setUsername(hikariConfig.getUsername());
        properties.setPassword(hikariConfig.getPassword());
        return properties;
    }

    @Bean(name = "vpsDataSource")
    public HikariDataSource vpsDataSource() {
        return new HikariDataSource(vpsHikariConfig());
    }

    @Bean(name = "vpsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean vpsEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(vpsDataSource())
                .packages("com.viettel.hstd.entity.vps", "com.viettel.hstd.core.entity", "com.viettel.hstd.constant")
                .build();
    }

    @Bean(name = "vpsTransactionManager")
    public PlatformTransactionManager vpsTransactionManager(EntityManagerFactoryBuilder builder) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(vpsEntityManagerFactory(builder).getObject());
        tm.setDataSource(vpsDataSource());
        return tm;
    }
}
