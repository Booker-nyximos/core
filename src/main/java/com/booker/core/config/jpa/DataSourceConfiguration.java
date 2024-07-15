package com.booker.core.config.jpa;

import com.booker.core.annotation.UseDatasource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;

@UseDatasource
@Configuration
public class DataSourceConfiguration {

    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource() {
        return new LazyConnectionDataSourceProxy(DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build());
    }
}
