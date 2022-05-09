package com.viettel.hstd.core.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
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

@Configuration
@EnableJpaRepositories(basePackages = {"com.viettel.hstd.repository.hstd"},
        entityManagerFactoryRef = "hstdEntityManagerFactory",
        transactionManagerRef= "hstdTransactionManager")

public class HSTDDatasourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.hstd")
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        return new HikariConfig();
    }

    @Bean
    @Primary
    public DataSourceProperties hstdDataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        HikariConfig hikariConfig = new HikariConfig();
        properties.setDriverClassName(hikariConfig.getDriverClassName());
        properties.setUrl(hikariConfig.getJdbcUrl());
        properties.setUsername(hikariConfig.getUsername());
        properties.setPassword(hikariConfig.getPassword());
        return properties;
    }

    @Bean(name = "hstdDataSource")
    @Primary
    public HikariDataSource hstdDataSource() {
        return new HikariDataSource(hikariConfig());
    }

    @Primary
    @Bean(name = "hstdEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean hstdEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(hstdDataSource())
                .packages("com.viettel.hstd.entity.hstd", "com.viettel.hstd.core.entity", "com.viettel.hstd.constant")
                .build();
    }

    @Primary
    @Bean(name = "hstdTransactionManager")
    public PlatformTransactionManager hstdTransactionManager(EntityManagerFactoryBuilder builder) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(hstdEntityManagerFactory(builder).getObject());
        tm.setDataSource(hstdDataSource());
        return tm;
    }
}
